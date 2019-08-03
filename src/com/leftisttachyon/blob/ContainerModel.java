package com.leftisttachyon.blob;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.util.LinkedList;
import java.util.List;

import com.microsoft.azure.storage.blob.ContainerURL;
import com.microsoft.azure.storage.blob.ListBlobsOptions;
import com.microsoft.azure.storage.blob.PipelineOptions;
import com.microsoft.azure.storage.blob.ServiceURL;
import com.microsoft.azure.storage.blob.SharedKeyCredentials;
import com.microsoft.azure.storage.blob.StorageURL;
import com.microsoft.azure.storage.blob.models.BlobItem;
import com.microsoft.azure.storage.blob.models.ContainerCreateResponse;
import com.microsoft.azure.storage.blob.models.ContainerListBlobFlatSegmentResponse;
import com.microsoft.rest.v2.RestException;

import io.reactivex.Single;

public final class ContainerModel implements Serializable {
	private static final long serialVersionUID = -111L;
	
	private static final String ACCOUNT_NAME = "shinzoblob";
	private static final String ACCOUNT_KEY = "+YleewDvxVMf1zc22A41Xld1ZFT5UBtuO6Efz3QlHN1OFANdGbWTehe1zie1RSULlhK9ouA4lZ1Hu2jZdSNb/A==";
	private static final SharedKeyCredentials CREDS;
	static {
		SharedKeyCredentials tempCreds = null;
		try {
			tempCreds = new SharedKeyCredentials(ACCOUNT_NAME, ACCOUNT_KEY);
		} catch (InvalidKeyException e) {
			System.out.println("Invalid Storage account name/key provided");
		}
		CREDS = tempCreds;
	}

	private final ServiceURL serviceURL;
	private ContainerURL containerURL;

	public ContainerModel(String containerName) throws MalformedURLException {
		this(containerName, true);
	}
	
	public ContainerModel(String containerName, boolean checkForCreation) throws MalformedURLException {
		if (CREDS == null) {
			throw new IllegalStateException("You cannot create a blob without credentials");
		}

		serviceURL = new ServiceURL(new URL("https://" + ACCOUNT_NAME + ".blob.core.windows.net"),
				StorageURL.createPipeline(CREDS, new PipelineOptions()));

		containerURL = serviceURL.createContainerURL(containerName);

		if(checkForCreation) {
			try {
				ContainerCreateResponse response = containerURL.create(null, null, null).blockingGet();
				System.out.println("Container Create Response was " + response.statusCode());
			} catch (RestException e) {
				if (e.response().statusCode() != 409) {
					throw e;
				} else {
					System.out.println(containerName + " container already exists, resuming...");
				}
			}
		}
	}

	public BlobModel createBlob(String blobName) throws MalformedURLException {
		return new BlobModel(containerURL, blobName);
	}
	
	public BlobModel createBlob(BlobItem item) throws MalformedURLException {
		return new BlobModel(containerURL, item.name());
	}

	public void listBlobs() {
		try {
			listBlobs(containerURL);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static Single<ContainerListBlobFlatSegmentResponse> listAllBlobs(ContainerURL url,
			ContainerListBlobFlatSegmentResponse response) {
		// Process the blobs returned in this result segment (if the segment is empty,
		// blobs() will be null.
		if (response.body().segment() != null) {
			for (BlobItem b : response.body().segment().blobItems()) {
				String output = "Blob name: " + b.name();
				if (b.snapshot() != null) {
					output += ", Snapshot: " + b.snapshot();
				}
				System.out.println(output);
			}
		} else {
			System.out.println("There are no more blobs to list off.");
		}

		// If there is not another segment, return this response as the final response.
		if (response.body().nextMarker() == null) {
			return Single.just(response);
		} else {
			/*
			 * IMPORTANT: ListBlobsFlatSegment returns the start of the next segment; you
			 * MUST use this to get the next segment (after processing the current result
			 * segment
			 */

			String nextMarker = response.body().nextMarker();

			/*
			 * The presence of the marker indicates that there are more blobs to list, so we
			 * make another call to listBlobsFlatSegment and pass the result through this
			 * helper function.
			 */
			return url.listBlobsFlatSegment(nextMarker, new ListBlobsOptions().withMaxResults(10), null).flatMap(
					containersListBlobFlatSegmentResponse -> listAllBlobs(url, containersListBlobFlatSegmentResponse));
		}
	}

	static void listBlobs(ContainerURL containerURL) throws InterruptedException {
		// Each ContainerURL.listBlobsFlatSegment call return up to maxResults
		// (maxResults=10 passed into ListBlobOptions below).
		// To list all Blobs, we are creating a helper static method called
		// listAllBlobs,
		// and calling it after the initial listBlobsFlatSegment call
		ListBlobsOptions options = new ListBlobsOptions();
		options.withMaxResults(10);

		final Object LOCK = new Object();

		containerURL.listBlobsFlatSegment(null, options, null)
				.flatMap(containerListBlobFlatSegmentResponse -> listAllBlobs(containerURL,
						containerListBlobFlatSegmentResponse))
				.subscribe(response -> {
					System.out.println("Completed list blobs request.");
					System.out.println(response.statusCode());
					synchronized (LOCK) {
						LOCK.notify();
					}
				});

		synchronized (LOCK) {
			LOCK.wait();
		}
	}

	public List<BlobItem> blobList() {
		try {
			return blobList(containerURL);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}

	static List<BlobItem> blobList(ContainerURL containerURL) throws InterruptedException {
		List<BlobItem> list = new LinkedList<>();
		// Each ContainerURL.listBlobsFlatSegment call return up to maxResults
		// (maxResults=10 passed into ListBlobOptions below).
		// To list all Blobs, we are creating a helper static method called
		// listAllBlobs,
		// and calling it after the initial listBlobsFlatSegment call
		ListBlobsOptions options = new ListBlobsOptions();
		options.withMaxResults(10);

		final Object LOCK = new Object();

		containerURL.listBlobsFlatSegment(null, options, null)
				.flatMap(containerListBlobFlatSegmentResponse -> listAllBlobs(containerURL,
						containerListBlobFlatSegmentResponse, list))
				.subscribe(response -> {
					System.out.println("Completed request to create list of blobs.");
					System.out.println(response.statusCode());
					synchronized (LOCK) {
						LOCK.notify();
					}
				});

		synchronized (LOCK) {
			LOCK.wait();
		}
		return list;
	}

	private static Single<ContainerListBlobFlatSegmentResponse> listAllBlobs(ContainerURL url,
			ContainerListBlobFlatSegmentResponse response, List<BlobItem> toAdd) {
		// Process the blobs returned in this result segment (if the segment is empty,
		// blobs() will be null.
		if (response.body().segment() != null) {
			toAdd.addAll(response.body().segment().blobItems());
		}
		// If there is not another segment, return this response as the final response.
		if (response.body().nextMarker() == null) {
			return Single.just(response);
		} else {
			/*
			 * IMPORTANT: ListBlobsFlatSegment returns the start of the next segment; you
			 * MUST use this to get the next segment (after processing the current result
			 * segment
			 */

			String nextMarker = response.body().nextMarker();

			/*
			 * The presence of the marker indicates that there are more blobs to list, so we
			 * make another call to listBlobsFlatSegment and pass the result through this
			 * helper function.
			 */
			return url.listBlobsFlatSegment(nextMarker, new ListBlobsOptions().withMaxResults(10), null).flatMap(
					containersListBlobFlatSegmentResponse -> listAllBlobs(url, containersListBlobFlatSegmentResponse));
		}
	}
}