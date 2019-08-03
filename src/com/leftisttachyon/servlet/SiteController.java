package com.leftisttachyon.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.leftisttachyon.blob.BlobModel;
import com.leftisttachyon.blob.ContainerModel;
import com.leftisttachyon.objects.Organization;

/**
 * Servlet implementation class SiteController
 */
@WebServlet("/site")
public class SiteController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private List<Organization> list;

	private ContainerModel container;

	private BlobModel blob;

	public SiteController() throws IOException {
		container = new ContainerModel("organizations");
		blob = container.createBlob("organizations.dat");
		File f = blob.getBlob();
		try (FileInputStream fis = new FileInputStream(f); ObjectInputStream ois = new ObjectInputStream(fis)) {
			list = (List<Organization>) ois.readObject();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (list == null) {
			list = new ArrayList<>();
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String page = request.getParameter("page");
		switch (page) {
		case "explorer":

			break;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		switch (request.getParameter("actions")) {
		case "nextExplorer":
			if (list.isEmpty()) {
				response.getOutputStream().println("<p style=\"text-align: center;\">There are no organizations to display.</p>");
				return;
			}
			
			int num = Integer.parseInt(request.getParameter("num"));
			num %= list.size();
			response.getOutputStream().println(list.get(num).toString());
			break;
		}
	}

}
