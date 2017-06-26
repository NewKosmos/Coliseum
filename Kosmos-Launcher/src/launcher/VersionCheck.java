package launcher;

import java.io.*;
import java.net.*;

public class VersionCheck {
	private int remoteMajVer;
	private int remoteMinVer;
	private int remoteBuildVer;

	/**
	 * Creates a new thread for a version checker.
	 */
	public VersionCheck() {
		int count = 0;

		while (count <= 3 && remoteBuildVer == 0) {
			BufferedReader in = null;

			try {
				URL url = new URL(KosmosLauncher.VERSION_PAGE);
				HttpURLConnection http = (HttpURLConnection) url.openConnection();
				http.addRequestProperty("User-Agent", "Mozilla/4.76");
				InputStreamReader streamReader = new InputStreamReader(http.getInputStream());
				in = new BufferedReader(streamReader);
				String str;
				String str2[];

				while ((str = in.readLine()) != null) {
					if (str.contains("Version")) {
						str = str.replace("Version=", "");
						str2 = str.split("#");
						System.out.println("Remote Version: " + str2[0] + "." + str2[1] + "." + str2[2]);

						if (str2.length == 3) {
							remoteMajVer = Integer.parseInt(str2[0]);
							remoteMinVer = Integer.parseInt(str2[1]);
							remoteBuildVer = Integer.parseInt(str2[2]);
						}
					}
				}

				in.close();
				streamReader.close();
			} catch (Exception e) {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}

			count++;
		}
	}

	public int getRemoteMajVer() {
		return remoteMajVer;
	}

	public int getRemoteMinVer() {
		return remoteMinVer;
	}

	public int getRemoteBuildVer() {
		return remoteBuildVer;
	}
}