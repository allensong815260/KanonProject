/*
The contents of this file are subject to the Common Public Attribution License
Version 1.0 (the "License"); you may not use this file except in compliance with
the License. You may obtain a copy of the License at
http://www.projity.com/license . The License is based on the Mozilla Public
License Version 1.1 but Sections 14 and 15 have been added to cover use of
software over a computer network and provide for limited attribution for the
Original Developer. In addition, Exhibit A has been modified to be consistent
with Exhibit B.

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for the
specific language governing rights and limitations under the License. The
Original Code is OpenProj. The Original Developer is the Initial Developer and
is Projity, Inc. All portions of the code written by Projity are Copyright (c)
2006, 2007. All Rights Reserved. Contributors Projity, Inc.

Alternatively, the contents of this file may be used under the terms of the
Projity End-User License Agreeement (the Projity License), in which case the
provisions of the Projity License are applicable instead of those above. If you
wish to allow use of your version of this file only under the terms of the
Projity License and not to allow others to use your version of this file under
the CPAL, indicate your decision by deleting the provisions above and replace
them with the notice and other provisions required by the Projity  License. If
you do not delete the provisions above, a recipient may use your version of this
file under either the CPAL or the Projity License.

[NOTE: The text of this license may differ slightly from the text of the notices
in Exhibits A and B of the license at http://www.projity.com/license. You should
use the latest text at http://www.projity.com/license for your modifications.
You may not remove this license text from the source files.]

Attribution Information: Attribution Copyright Notice: Copyright � 2006, 2007
Projity, Inc. Attribution Phrase (not exceeding 10 words): Powered by OpenProj,
an open source solution from Projity. Attribution URL: http://www.projity.com
Graphic Image as provided in the Covered Code as file:  openproj_logo.png with
alternatives listed on http://www.projity.com/logo

Display of Attribution Information is required in Larger Works which are defined
in the CPAL as a work which combines Covered Code or portions thereof with code
not governed by the terms of the CPAL. However, in addition to the other notice
obligations, all copies of the Covered Code in Executable and Source Code form
distributed must, as a form of attribution of the original author, include on
each user interface screen the "OpenProj" logo visible to all users.  The
OpenProj logo should be located horizontally aligned with the menu bar and left
justified on the top left of the screen adjacent to the File menu.  The logo
must be at least 100 x 25 pixels.  When users click on the "OpenProj" logo it
must direct them back to http://www.projity.com.
*/
package com.projity.util;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.projity.strings.Messages;

public class VersionUtils {
	private static Log log = LogFactory.getLog(VersionUtils.class);
	public static String getVersion(){
		String version=null;
		try {

			ResourceBundle bundle = ResourceBundle.getBundle("org.ultimania.kanon.version");
			if (bundle!=null) version=bundle.getString("kanon.version");

		} catch (Exception e) {
			log.error("Could not found org.ultimania.kanon.version.properties file.");
		}
		return version;
	}
	public static String getJnlpVersion(){
		return System.getProperty("kanon.version");
	}

	public static String toAppletVersion(String v){
		StringBuffer sb=new StringBuffer();
		String vNumbers[]=v.split("\\.");
		for (int i=0;i<4;i++){
			int vn=(i>=vNumbers.length)?0:Integer.parseInt(vNumbers[i]);
			if (i>0) sb.append('.');
			String hex=Integer.toHexString(vn);
			for (int j=0;j<hex.length()-4;j++) sb.append('0');
			sb.append(hex);
		}
		return sb.toString();
	}

	public static boolean isJnlpUpToDate(){
		String v=getVersion();
		String jv=getJnlpVersion();
		if (v==null||jv==null) return true;
		try{
			return jv.equals(toAppletVersion(v));
		}catch(Exception e){return false;}
	}
	public static boolean versionCheck(boolean warnIfBad) {
		String version = VersionUtils.getVersion();
		if (version == null) // for running in debugger
			version="0";
		Preferences pref=Preferences.userNodeForPackage(VersionUtils.class);
		String localVersion = pref.get("Kanon Version","unknown");
		boolean updated = !localVersion.equals(version);
		String javaVersion = System.getProperty("java.version");
		log.info("Kanon Version: "+version + " local version " + localVersion + " updated=" + updated + " java version=" + javaVersion);


		pref.put("JavaVersion",javaVersion);

		if (updated) {
			Environment.setUpdated(true);
			pref.put("PODVersion",version);
			try {
				pref.flush();
			} catch (BackingStoreException e) {
				e.printStackTrace();
			}

			if (warnIfBad && Environment.isApplet()) {
				if (javaVersion.equals("1.6.0_09") || javaVersion.equals("1.6.0_08") || javaVersion.equals("1.6.0_07")|| javaVersion.equals("1.6.0_06")|| javaVersion.equals("1.6.0_05") || javaVersion.equals("1.6.0_04")) {
					Environment.setNeedToRestart(true);
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							Alert.error(Messages.getString("Error.restart"));
						}});
				}
			}
		}else{
			try {
				pref.flush();
			} catch (BackingStoreException e) {
				e.printStackTrace();
			}
		}
		return updated;
	}


}
