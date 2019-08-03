package com.leftisttachyon.blob;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.StandardOpenOption;

import com.microsoft.azure.storage.blob.BlockBlobURL;
import com.microsoft.azure.storage.blob.ContainerURL;
import com.microsoft.azure.storage.blob.TransferManager;

public class BlobModel implements Closeable, Serializable {
	
	private static final long serialVersionUID = -11L;

	// The M in MVC
	static void uploadFile(BlockBlobURL blob, File sourceFile) throws IOException, InterruptedException {
		AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(sourceFile.toPath());

		// Uploading a file to the blobURL using the high-level methods available in
		// TransferManager class
		// Alternatively call the PutBlob/PutBlock low-level methods from BlockBlobURL
		// type
		final Object LOCK = new Object();
		TransferManager.uploadFileToBlockBlob(fileChannel, blob, 8 * 1024 * 1024, null, null).subscribe(response -> {
			System.out.println("Completed upload request.");
			System.out.println(response.response().statusCode());
			synchronized (LOCK) {
				LOCK.notify();
			}
		});

		synchronized (LOCK) {
			LOCK.wait();
		}
	}

	static void deleteBlob(BlockBlobURL blobURL) throws InterruptedException {
		// Delete the blob
		final Object LOCK = new Object();
		blobURL.delete(null, null, null).subscribe(response -> {
			System.out.println(">> Blob deleted: " + blobURL);
			synchronized (LOCK) {
				LOCK.notify();
			}
		}, error -> {
			System.out.println(">> An error encountered during deleteBlob: " + error.getMessage());
			synchronized (LOCK) {
				LOCK.notify();
			}
		});

		synchronized (LOCK) {
			LOCK.wait();
		}
	}

	static void getBlob(BlockBlobURL blobURL, File sourceFile) throws IOException, InterruptedException {
		AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(sourceFile.toPath(),
				StandardOpenOption.CREATE, StandardOpenOption.WRITE);
		final Object LOCK = new Object();

		TransferManager.downloadBlobToFile(fileChannel, blobURL, null, null).subscribe(response -> {
			System.out.println("Completed download request.");
			System.out.println("The blob was downloaded to " + sourceFile.getAbsolutePath());
			synchronized (LOCK) {
				LOCK.notify();
			}
		});

		synchronized (LOCK) {
			LOCK.wait();
		}
	}

	// private final ServiceURL serviceURL;

	// private final ContainerURL containerURL;

	private final BlockBlobURL blobURL;

	// Ctrl + c, Ctrl + v from Microsoft's example
	// Why reinvent the wheel?

	BlobModel(ContainerURL containerURL, String blobName) throws MalformedURLException {
		// this.serviceURL = serviceURL;
		blobURL = containerURL.createBlockBlobURL(blobName);
	}

	public void uploadFile(File toUpload) throws IOException {
		try {
			uploadFile(blobURL, toUpload);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	File download = null;

	public File getBlob() throws IOException {
		download = File.createTempFile("savestate", ".schproj");
		try {
			getBlob(blobURL, download);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return download;
	}

	public void deleteBlob() {
		try {
			deleteBlob(blobURL);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		if (download != null) {
			download.delete();
		}
	}
}