/*
 * Copyright (C) 2008 Digital Sundhed (SDSD)
 *
 * All source code and information supplied as part of chronos
 * is copyright to its contributers.
 *
 * The source code has been released under a dual license - meaning you can
 * use either licensed version of the library with your code.
 *
 * It is released under the Common Public License 1.0, a copy of which can
 * be found at the link below.
 * http://www.opensource.org/licenses/cpl.php
 *
 * It is released under the LGPL (GNU Lesser General Public License), either
 * version 2.1 of the License, or (at your option) any later version. A copy
 * of which can be found at the link below.
 * http://www.gnu.org/copyleft/lesser.html
 */
package org.codehaus.mojo.chronos.report;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.codehaus.doxia.sink.Sink;

/**
 * Utility calss to assist in report generation.
 * 
 * @author ksr@lakeside.dk
 */
class ReportSink {
    private ResourceBundle bundle;

    private Sink sink;

    public ReportSink(ResourceBundle bundle, Sink sink) {
        this.bundle = bundle;
        this.sink = sink;
    }

    void constructHeaderSection(String title, String description, String anchor) {
        if(title != null) {
            sink.sectionTitle1();
            sinkAnchor(anchor);
            sink.text(title);
            sink.sectionTitle1_();
        }
        if(description != null) {
            sink.rawText(description);
            sinkLineBreak();
        }
    }

    void title2(String text, String anchor) {
        sink.sectionTitle2();
        sinkAnchor(anchor);
        sink.text(text);
        sink.sectionTitle2_();
    }

    void title3(String text, String anchor) {
        sink.sectionTitle3();
        sinkAnchor(anchor);
        sink.text(text);
        sink.sectionTitle3_();

    }

    void graphics(String name) {
        try {
            sink.figure();
            String encodedName = URLEncoder.encode(name, "UTF-8");
            sink.figureGraphics("images" + File.separatorChar + encodedName);
            sink.figure_();
            sinkLineBreak();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    void sinkLineBreak() {
        sink.lineBreak();
        sink.lineBreak();
    }

    void table(List headerLabels, List dataLines) {
        sink.table();
        sink.tableRow();
        Iterator it = headerLabels.iterator();
        while (it.hasNext()) {
            String headerLabel = (String)it.next();
            th(headerLabel);
        }
        sink.tableRow_();
        Iterator data = dataLines.iterator();
        while (data.hasNext()) {
            Object next = data.next();
            List dataLine = (List)next;
            sink.tableRow();
            Iterator items = dataLine.iterator();
            while (items.hasNext()) {
                String item = (String)items.next();
                sinkCell(item);
            }
            sink.tableRow_();
        }
        sink.table_();
        sinkLineBreak();
    }

    void th(String key) {
        sink.tableHeaderCell();
        sink.text(bundle.getString(key));
        sink.tableHeaderCell_();
    }

    void sinkCell(String text) {
        sink.tableCell();
        sink.text(text);
        sink.tableCell_();
    }

    void sinkLink(String text, String link) {
        sink.rawText("[");
        sink.link("#" + link);
        sink.text(text);
        sink.link_();
        sink.rawText("]");
    }

    void sinkCellLink(String text, String link) {
        sink.tableCell();
        sink.link(link);
        sink.text(text);
        sink.link_();
        sink.tableCell_();
    }

    void sinkAnchor(String anchor) {
        sink.anchor(anchor);
        sink.anchor_();
    }
}
