package kobeU.cs.kadaiB.util;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Date;

public class ObjMessage2 implements Serializable {

	private static final long serialVersionUID = 3249940705431916304L;
	public final String body;
	public final Date  date;
	public ObjMessage2(String body) {
		this.body = body;
		this.date = new Date();
	}
	public String toString() {
		StringWriter out0 = new StringWriter();
		PrintWriter out = new PrintWriter(out0);
		out.println("[ObjMsg:" + body + ",");
		out.println(" Date:" + date);
		out.close();
		return out0.toString();
	}
}
