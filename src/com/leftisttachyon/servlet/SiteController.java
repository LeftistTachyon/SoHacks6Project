package com.leftisttachyon.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
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
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (list == null) {
			list = new ArrayList<>();
		}
	}
	
	public static void main(String[] args) throws IOException {
		ArrayList<Organization> list = new ArrayList<>();
		
		list.add(new Organization("https://lh6.googleusercontent.com/6umES_4KmKGtmNyaxmZovGYtddZOGxif9VHi88rJcAAd6J5YVaATsPiu2_Tui74dgK3DlSnJVk4eStPIqIDnqe98QgZaFyuX4LHPEvYHLw",
				"Sadly, our Texas beaches have been polluted with garbage that ruins the scenery. We host beach clean-ups around the Galveston monthly. Hope to see some of y’all there!",
				"Clean Up Stewart Beach"));
		list.add(new Organization("https://lh6.googleusercontent.com/6gxQzTMl4GgEnwKksVeRtu13GRzpkn7MQeR21gN2HtRW_8qNpTb1LZ0WTi13aL6idWtLjyB8ZB1d9biMonAabozm53rFbxJrr4m6nw3vAQ", 
				"To help feed the San Antonio Community Garden, we are calling on residents to give us their food scraps. Just freeze plant-based food and drop it off in front of the garden. Thanks!", 
				"Community Composting"));
		list.add(new Organization("https://lh5.googleusercontent.com/oc0hZYsl_fSvQhhMRJRZ2dLywvkeFn-JdfV4TmYMnYAtkRcjAR6Ujzw2VndmeqzJiszD3K72X65Opk1V0iWoFdonxagTh7gqAsAm8iXXMlj1bf6FYc331az7BSkZPVRJqo5c-bc_", 
				"We will be hosting a weekly coding camp for middle and elementary students in Maud Marks Library for anyone who wants to learn coding basics. Please come teach if you have experience in any languages!", 
				"Youth Coding Camp"));
		list.add(new Organization("https://lh3.googleusercontent.com/r9K8kT_mMDs4VaPXrGlnhiaXsgsNvl4b-QwJAW6L5m0tTfqeAjw7EU3w_Qimme-Sn2ATKbcnShQlBtoqIKLESY3hJrSbnzq5IcespP-LUcQgeg523XdJQxogewJ_hXHgsvYsgrWi", 
				"Hey y’all!! We wanted to help give back to the homeless community. Whether you want to cook, serve or bring food, we would appreciate the help!", 
				"Commmunity Thanksgiving For The Homeless"));
		list.add(new Organization("https://lh5.googleusercontent.com/4g3lPQpN46jux5Nn56sxp7J0r7F-t4-QbonVypIMYpl0tQiufi8pdqa-Jw3mqc5Fy4Yj17VxSB6ySgjqQdbbgGtG45zxHf5Nzhk9fF2vhAAIALe1_3iRY-BqZEcCHBMszPyJfw5m", 
				"Seniors are the most likely demographic to download viruses on computers. Learning basic computer skill will protect their information. Please go to Oak Hill Homes on Sundays to teach Seniors!", 
				"Help Seniors Learn Computer Basics"));
		
		File temp = File.createTempFile("organizations", ".dat");
		try(FileOutputStream fos = new FileOutputStream(temp);
				ObjectOutputStream oos = new ObjectOutputStream(fos)) {
			oos.writeObject(list);
		}
		
		ContainerModel c = new ContainerModel("organizations");
		BlobModel b = c.createBlob("organizations.dat");
		b.uploadFile(temp);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("GET");
		String page = request.getParameter("page");
		if(page == null) {
			response.sendRedirect(request.getContextPath() + "/index.jsp");
		}
		switch (page) {
		case "explorer":
			request.getSession().setAttribute("orgList", list);
			request.getRequestDispatcher("/explorer.jsp").forward(request, response);
			break;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("POST");
		switch (request.getParameter("actions")) {
		case "nextExplorer":
			if (list.isEmpty()) {
				response.getOutputStream()
						.println("<p style=\"text-align: center;\">There are no organizations to display.</p>");
				return;
			}

			int num = Integer.parseInt(request.getParameter("num"));
			num %= list.size();
			response.getOutputStream().println(list.get(num).toString());
			break;
		}
	}

}
