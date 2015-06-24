package ec.com.mapache.ngflow.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ec.com.mapache.ngflow.upload.FlowInfo;
import ec.com.mapache.ngflow.upload.FlowInfoStorage;
import ec.com.mapache.ngflow.upload.HttpUtils;

/**
 *
 * This is a servlet demo, for using Flow.js to upload files.
 *
 * by fanxu123
 */
public class UploadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	/*
	 * Change this to your upload folder, by default we will use unix  /tmp
	 */
	public static final String UPLOAD_DIR = "/tmp";
	
	public static final String MYDOMAIN = "http://localhost:8080";



	/*
	 * In ORDER to allow CORS  to multiple domains you can set a list of valid domains here
	 */
	private List<String> authorizedUrl = Arrays.asList("http://localhost",
			"http://mydomain1.com", "https://mydomain1.com");

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("Do Post");

		System.out.println(request.getRequestURL());

		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		response.setHeader("Cache-control", "no-cache, no-store");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Expires", "-1");

		response.setHeader("Access-Control-Allow-Origin",
				"http://unika.localdomain");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods", "POST");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		response.setHeader("Access-Control-Max-Age", "86400");

			
		int flowChunkNumber = getflowChunkNumber(request);

		FlowInfo info = getFlowInfo(request);

		RandomAccessFile raf = new RandomAccessFile(info.flowFilePath, "rw");

		// Seek to position
		raf.seek((flowChunkNumber - 1) * info.flowChunkSize);

		// Save to file
		InputStream is = request.getInputStream();
		long readed = 0;
		long content_length = request.getContentLength();
		byte[] bytes = new byte[1024 * 100];
		while (readed < content_length) {
			int r = is.read(bytes);
			if (r < 0) {
				break;
			}
			raf.write(bytes, 0, r);
			readed += r;
		}
		raf.close();

		// Mark as uploaded.
		info.uploadedChunks.add(new FlowInfo.flowChunkNumber(flowChunkNumber));
		String archivoFinal = info.checkIfUploadFinished();
		if (archivoFinal != null) { // Check if all chunks uploaded, and
			// change filename
			FlowInfoStorage.getInstance().remove(info);
			response.getWriter().print("All finished.");

		} else {
			response.getWriter().print("Upload");
		}
		// out.println(myObj.toString());

		out.close();
	}
		
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		int flowChunkNumber = getflowChunkNumber(request);
		System.out.println("Do Get");
		

		System.out.println(request.getRequestURL());
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		response.setHeader("Cache-control", "no-cache, no-store");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Expires", "-1");

		response.setHeader("Access-Control-Allow-Origin",
				"http://unika.localdomain");
		response.setHeader("Access-Control-Allow-Methods", "GET");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		response.setHeader("Access-Control-Max-Age", "86400");

		
		FlowInfo info = getFlowInfo(request);
		
		Object fcn = new FlowInfo.flowChunkNumber(flowChunkNumber);
		
		if (info.uploadedChunks.contains(fcn)) {
			System.out.println("Do Get arriba");
			response.getWriter().print("Uploaded."); // This Chunk has been
														// Uploaded.
		} else {
			System.out.println("Do Get something is wrong");
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}

		out.close();
	}

	private int getflowChunkNumber(HttpServletRequest request) {
		return HttpUtils.toInt(request.getParameter("flowChunkNumber"), -1);
	}

	private FlowInfo getFlowInfo(HttpServletRequest request)
			throws ServletException {

		System.out.println("getFlow");
		String base_dir = UPLOAD_DIR;

		int FlowChunkSize = HttpUtils.toInt(
				request.getParameter("flowChunkSize"), -1);
		
		long FlowTotalSize = HttpUtils.toLong(
				request.getParameter("flowTotalSize"), -1); //flowTotalChunks
		String FlowIdentifier = request.getParameter("flowIdentifier");
		String FlowFilename = request.getParameter("flowFilename");
		String FlowRelativePath = request.getParameter("flowRelativePath");
		// Here we add a ".temp" to every upload file to indicate NON-FINISHED
		String FlowFilePath = new File(base_dir, FlowFilename)
				.getAbsolutePath() + ".temp";

		FlowInfoStorage storage = FlowInfoStorage.getInstance();

		FlowInfo info = storage.get(FlowChunkSize, FlowTotalSize,
				FlowIdentifier, FlowFilename, FlowRelativePath, FlowFilePath);
		if (!info.valid()) {
			storage.remove(info);
			throw new ServletException("Invalid request params.");
		}
		return info;
	}

	
}