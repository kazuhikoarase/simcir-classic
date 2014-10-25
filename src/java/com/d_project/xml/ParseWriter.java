package com.d_project.xml;

import java.io.BufferedWriter;
import java.io.Writer;

/**
 * ParseWriter
 * @author Kazuhiko Arase
 */
public class ParseWriter extends BufferedWriter {
    public ParseWriter(Writer writer) {
        super(writer);
    }
}

