package simcir;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import com.d_project.xml.Document;
import com.d_project.xml.Element;
import com.d_project.xml.ParseReader;
import com.d_project.xml.ParseWriter;

/**
 * CircuitDocument
 * @author Kazuhiko Arase
 */
class CircuitDocument extends Document {

    public CircuitDocument(Element top) {
        super(top);
    }

    public CircuitDocument(URL url) throws IOException {
        this(url.openStream() );
    }

    public CircuitDocument(InputStream is) throws IOException {
        super(new ParseReader(new BufferedReader(new InputStreamReader(is) ) ) );//url.openStream() ) ) );
    }


    public void write(ParseWriter writer) throws IOException {
        writer.write("<!-- SIMCIR 1.2.1 -->");
        writer.newLine();
        writer.write("<!-- Don't edit this file. -->");
        writer.newLine();
        super.write(writer);
    }
}

