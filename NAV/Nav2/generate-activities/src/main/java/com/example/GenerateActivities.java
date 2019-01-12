package com.example;

import java.io.File;
import java.io.PrintWriter;

public class GenerateActivities {
	private static final boolean parentNav = true;
	private static final String packageName = "com.javadude.nav2";
	private static final File base = new File("app/src/main/java/" + packageName.replace('.', '/'));
	private static class Node {
		private String id;
		private String name;
		private Node[] children;
		private Node parent;

		public Node(String id, String name, Node... children) {
			this.id = id;
			this.name = name;
			this.children = children;
			for(Node child: children) {
				child.parent = this;
			}
		}
	}
	public static void main(String[] args) throws Throwable {
		base.mkdirs();
		System.out.println(System.getProperty("user.dir"));
		Node top = new Node("Main", "Movies R Us!",
				new Node("Action", "Action Movies",
						new Node("Statham1", "Jason Statham Kicks Someone"),
						new Node("Statham2", "Jason Statham Kicks Someone Else"),
						new Node("Statham3", "Jason Statham Drives a Car"),
						new Node("Statham4", "Jason Statham Takes Off His Shirt")
				),
				new Node("Horror", "Horror Movies",
						new Node("Friday402", "Friday the Thirteen, part 402"),
						new Node("Friday403", "Friday the Thirteen, part 403"),
						new Node("Friday404", "Friday the Thirteen, part 404"),
						new Node("Friday405", "Friday the Thirteen, part 405")
				),
				new Node("Scifi", "Science Fiction Movies",
						new Node("Star1", "Star Battles, Episode 1: Too Much Talk"),
						new Node("Star2", "Star Battles, Episode 2: The Cash Grab"),
						new Node("Star3", "Star Battles, Episode 3: I Laughed, I Cried; It Was Better Than CATS")
				)
		);

		genActivity(top);
		genLayout(top);
		genManifest(top);
	}

	private static void genManifest(Node node) throws Throwable {
		PrintWriter pw = new PrintWriter("app/src/main/AndroidManifest.xml");

		pw.println("<?xml version='1.0' encoding='utf-8'?>");
		pw.println("<manifest xmlns:android='http://schemas.android.com/apk/res/android'");
		pw.println("\tpackage='" + packageName + "'>");

		pw.println("\t<application");
		pw.println("\t\tandroid:allowBackup='true'");
		pw.println("\t\tandroid:icon='@mipmap/ic_launcher'");
		pw.println("\t\tandroid:label='@string/app_name'");
		pw.println("\t\tandroid:supportsRtl='true'");
		pw.println("\t\tandroid:theme='@style/AppTheme'>");
		pw.println("\t\t<activity android:name='." + node.id + "Activity' android:label='" + node.name + "'>");
		pw.println("\t\t\t<intent-filter>");
		pw.println("\t\t\t\t<action android:name='android.intent.action.MAIN'/>");
		pw.println("\t\t\t\t<category android:name='android.intent.category.LAUNCHER'/>");
		pw.println("\t\t\t</intent-filter>");
		pw.println("\t\t</activity>");
		addChildActivities(pw, node);
		pw.println("\t</application>");
		pw.println("</manifest>");
		pw.close();
	}

	private static void addChildActivities(PrintWriter pw, Node node) {
		for(Node child : node.children) {
			pw.print("\t\t<activity android:name='." + child.id + "Activity' android:label='" + child.name + "'");
			if (!parentNav) {
				pw.println("/>");
			} else {
				pw.println(" android:parentActivityName='." + child.parent.id + "Activity'>");
				pw.println("\t\t\t<meta-data");
				pw.println("\t\t\t\tandroid:name='android.support.PARENT_ACTIVITY'");
				pw.println("\t\t\t\tandroid:value='." + child.parent.id + "Activity' />");
				pw.println("\t\t</activity>");
			}
			addChildActivities(pw, child);
		}
	}

	private static void genLayout(Node node) throws Throwable {
		PrintWriter pw = new PrintWriter("app/src/main/res/layout/" + node.id.toLowerCase() + ".xml");

		pw.println("<?xml version='1.0' encoding='utf-8'?>");
		pw.println("<LinearLayout");
		pw.println("\txmlns:android='http://schemas.android.com/apk/res/android'");
		pw.println("\tandroid:orientation='vertical'");
		pw.println("\tandroid:layout_width='match_parent'");
		pw.println("\tandroid:layout_height='match_parent'");
		pw.println("\tandroid:paddingBottom='@dimen/activity_vertical_margin'");
		pw.println("\tandroid:paddingLeft='@dimen/activity_horizontal_margin'");
		pw.println("\tandroid:paddingRight='@dimen/activity_horizontal_margin'");
		pw.println("\tandroid:paddingTop='@dimen/activity_vertical_margin'>");

		if (node.children.length == 0) {
			if (node.name.endsWith("404")) {
				pw.println("<TextView");
				pw.println("\tandroid:layout_width='wrap_content'");
				pw.println("\tandroid:layout_height='wrap_content'");
				pw.println("\tandroid:text='NOT FOUND'/>");
			} else {
				pw.println("<ImageView");
				pw.println("\tandroid:layout_weight='1'");
				pw.println("\tandroid:layout_width='match_parent'");
				pw.println("\tandroid:layout_height='0dp'");
				pw.println("\tandroid:src='@drawable/ic_movie_black_24dp'/>");
			}
		}

		for(Node child : node.children) {
			pw.println("<TextView");
			pw.println("\tandroid:onClick='goto" + child.id + "'");
			pw.println("\tandroid:text='" + child.name + "'");
			pw.println("\tandroid:layout_width='match_parent'");
			pw.println("\tandroid:layout_height='wrap_content'/>");
		}

		if (node.parent != null) {
			pw.println("<View");
			pw.println("android:layout_width='fill_parent'");
			pw.println("android:layout_height='2dp'");
			pw.println("android:background='#c0c0c0'/>");

			pw.println("<TextView");
			pw.println("\tandroid:text='You might like...'");
			pw.println("\tandroid:textSize='30sp'");
			pw.println("\tandroid:layout_width='match_parent'");
			pw.println("\tandroid:layout_height='wrap_content'/>");
			for(Node child : node.parent.children) {
				if (child != node) {
					pw.println("<TextView");
					pw.println("\tandroid:onClick='goto" + child.id + "'");
					pw.println("\tandroid:text='" + child.name + "'");
					pw.println("\tandroid:layout_width='match_parent'");
					pw.println("\tandroid:layout_height='wrap_content'/>");
				}
			}
		}
		pw.println("</LinearLayout>");
		pw.close();
		for(Node child : node.children) {
			genLayout(child);
		}
	}

	private static void genActivity(Node node) throws Throwable {
		PrintWriter pw = new PrintWriter(base.getPath() + "/" + node.id + "Activity.java");
		pw.println("package " + packageName + ";");

		pw.println("import android.content.Intent;");
		pw.println("import android.os.Bundle;");
		pw.println("import android.support.v7.app.AppCompatActivity;");
		pw.println("import android.view.MenuItem;");
		pw.println("import android.view.View;");
		pw.println("import " + packageName + ".R;");
		if (parentNav && node.parent != null)
			pw.println("import android.support.v4.app.NavUtils;");

		pw.println("public class " + node.id + "Activity extends AppCompatActivity {");
		pw.println("\t@Override");
		pw.println("\tprotected void onCreate(Bundle savedInstanceState) {");
		pw.println("\t\tsuper.onCreate(savedInstanceState);");
		pw.println("\t\tsetContentView(R.layout." + node.id.toLowerCase() + ");");
		if (parentNav && node.parent != null)
			pw.println("\t\tgetSupportActionBar().setDisplayHomeAsUpEnabled(true);");
		pw.println("\t}");
		if (parentNav && node.parent != null) {
			pw.println("\t@Override");
			pw.println("\tpublic boolean onOptionsItemSelected(MenuItem item) {");
			pw.println("\t\tswitch (item.getItemId()) {");
			pw.println("\t\t\tcase android.R.id.home:");
			pw.println("\t\t\t\tNavUtils.navigateUpFromSameTask(this);");
			pw.println("\t\t\t\treturn true;");
			pw.println("\t\t\tdefault:");
			pw.println("\t\t\t\treturn super.onOptionsItemSelected(item);");
			pw.println("\t\t}");
			pw.println("\t}");
		}
		for(Node child : node.children) {
			pw.println("\tpublic void goto" + child.id + "(View view) {");
			pw.println("\t\tstartActivity(new Intent(this, " + child.id + "Activity.class));");
			pw.println("\t}");
		}
		if (node.parent != null) {
			for(Node child : node.parent.children) {
				if (child != node) {
					pw.println("\tpublic void goto" + child.id + "(View view) {");
					pw.println("\t\tstartActivity(new Intent(this, " + child.id + "Activity.class));");
					pw.println("\t}");
				}
			}
		}

		pw.println("}");
		pw.close();

		for(Node child : node.children) {
			genActivity(child);
		}
	}
}
