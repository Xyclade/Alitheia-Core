/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2008 by Sebastian Kuegler <sebas@kde.org>
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package eu.sqooss.webui.view;

import java.util.Collection;
import java.util.HashMap;
import java.util.SortedMap;

import eu.sqooss.webui.Functions;
import eu.sqooss.webui.ListView;
import eu.sqooss.webui.Metric;
import eu.sqooss.webui.Project;
import eu.sqooss.webui.Result;
import eu.sqooss.webui.datatype.File;

/**
 * The class <code>VerboseFileView</code> renders an HTML sequence that
 * verbosely presents the metric evaluation result of a single file in a
 * specific project version. In addition it provides mean for comparing the
 * results against the results calculated on this file in another project
 * revision.
 */
public class VerboseFileView extends ListView {
    /** Holds the project object. */
    private Project project;

    /*
     * Holds the Id of the file whose information will be used in the view's
     * rendering.
     */
    private Long fileId;

    /*
     * Holds the file's version number against which the results comparison
     * will be performed.
     */
    private Long compareToVersion;

    /**
     * Instantiates a new <code>VerboseFileView</code> object, and initializes
     * it with the given project object and file Id.
     * 
     * @param project the project object
     * @param fileId the file Id
     */
    public VerboseFileView(Project project, Long fileId) {
        super();
        this.project = project;
        this.fileId = fileId;
    }

    /**
     * This method enables the display of evaluation results comparison.
     * When the given version number is valid and not equal to the current
     * file's version, then the evaluation result's table generated by this
     * view will include the file's evaluation results in the given version
     * as well.
     * 
     * @param versionNumber the version number
     */
    public void compareAgainst(Long versionNumber) {
        this.compareToVersion = versionNumber;
    }

    /* (non-Javadoc)
     * @see eu.sqooss.webui.ListView#getHtml(long)
     */
    public String getHtml(long in) {
        if ((project == null) || (project.isValid() == false))
            return(sp(in) + Functions.error("Invalid project!"));
        // Hold the accumulated HTML content
        StringBuilder b = new StringBuilder("");
        // Holds the list of currently selected metrics
        Collection<String> mnemonics =
            project.getSelectedMetricMnemonics().values();
        // Holds the currently selected file's object
        File selFile = null;
        // Holds the evaluation results for the currently selected file
        HashMap<String, Result> selFileResults = new HashMap<String, Result>();

        // Retrieve the selected file's object
        if (fileId != null)
            selFile = project.getCurrentVersion().getFile(fileId);
        // Retrieve the selected file's results
        if (selFile != null) {
            selFile.setTerrier(this.terrier);
            selFileResults = selFile.getResults(mnemonics);
        }

        if (selFile == null) {
            b.append(sp(in) + Functions.error("File not found!"));
        }
        else if (selFileResults.isEmpty()) {
            b.append(sp(in) + Functions.warning("No evaluation result."));
        }
        else if (mnemonics.isEmpty()) {
            b.append(sp(in) + Functions.warning("No selected metrics."));
        }
        else {
            // Retrieve the selected file's version and name
            Long fileVersion =
                project.getVersionById(selFile.getVersion()).getNumber();
            String fileName = selFile.getName();
            // Check if a comparison with another version is requested
            Long compareToFileId = null;
            boolean doCompare =
                ((compareToVersion != null)
                        && (compareToVersion.equals(fileVersion) == false));

            //================================================================
            // File information and comparison tool-bar
            //================================================================
            // Adjust the file name length
            String dirSeparator = "/";
            if (selFile.getShortName().length() <= maxStrLength) {
                while (fileName.length() > maxStrLength) {
                    if (fileName.contains(dirSeparator) == false) break;
                    fileName = fileName.substring(
                            fileName.indexOf(dirSeparator) + 1,
                            fileName.length());
                }
                if (fileName.length() < selFile.getName().length())
                    fileName = ".../" + fileName;
            }
            else {
                fileName = ".../" + adjustRight(selFile.getShortName(), "...");
            }
            //----------------------------------------------------------------
            // Display the file name
            //----------------------------------------------------------------
            b.append(sp (in++) + "<form method=\"GET\" action=\""
                    + getServletPath() + "\">\n");
            b.append(sp(in) + "<span"
                    + " style=\"float: left; width: 60%; text-align:left;\">"
                    + "<b>Name: </b> " + fileName
                    + " (<i>" + selFile.getStatus().toLowerCase()
                    + " v." + fileVersion + "</i>)"
                    + "<input type=\"hidden\" name=\"fid\" value=\""
                    + selFile.getId() + "\">"
                    + "</span>\n");
            //----------------------------------------------------------------
            // Display the results comparison's input field
            //----------------------------------------------------------------
            SortedMap<Long, Long> mods = terrier.getFileModification(
                    project.getCurrentVersion().getId(), fileId);
            // Remove the current file's version from the modifications list
            mods.remove(fileVersion);
            /*
             * Enable the version selection field, unless the selected file's
             * modifications list is empty.
             */
            boolean disableSelect = mods.isEmpty();
            /*
             * Get the Id of the file against which the current file's results
             * will be compared. Note that both files do represent one and the
             * same file, just in different stages of its modification
             * history.
             */
            if (doCompare)
                compareToFileId = mods.get(compareToVersion);
            // Create the version selection field
            b.append(sp(in++) + "<span"
                    + " style=\"float: right; width: 40%; text-align:right;\""
                    + ">\n");
            b.append(sp(in) + "<b>Compare with:</b>\n");
            b.append(sp(in++) + "<select name=\"cvnum\" size=\"1\""
                    + ((disableSelect) ? " disabled" : "")
                    + " style=\"width:70px;\">\n");
            for (Long verNum : mods.keySet()) {
                /*
                 * Pre-select the first version that is bigger than the
                 * current one (if any). Since the "mods" map is sorted by
                 * version number (starting with the lowest value), this
                 * check will always select the nearest match.
                 */
                String selStatus = "";
                boolean selected = true;
                if ((fileVersion < verNum) && (selected)) {
                    selected = false;
                    selStatus = " selected";
                }
                // Dump the next version as option of the select tag
                b.append(sp(in) + "<option"
                        + selStatus
                        + " value=\"" + verNum + "\">"
                        + "v." + verNum
                        + "</option>\n");
            }
            b.append(sp(--in) + "</select>\n");
            b.append(sp(in) + "<input type=submit class=\"submit\""
                    + ((disableSelect) ? " disabled" : "")
                    + " value=\"Apply\">\n"
                    + sp (--in) + "</span>\n");
            b.append(sp(--in) + "</form>\n");

            //================================================================
            // Results (comparison) table
            //================================================================
            b.append(sp(in++) + "<div id=\"table\">\n");
            b.append(sp(in++) + "<table>\n");
            //----------------------------------------------------------------
            // Table header
            //----------------------------------------------------------------
            b.append(sp(in++) + "<thead>\n");
            b.append(sp(in++) + "<tr class=\"head\">\n");
            b.append(sp(in) + "<td class=\"head\" style=\"width: 15%;\">"
                    + "Metric</td>\n");
            b.append(sp(in) + "<td class=\"head\" style=\"width: 45%;\">"
                    + "Description</td>\n");
            if ((doCompare) && (compareToFileId != null)) {
                b.append(sp(in) + "<td class=\"head\" style=\"width: 20%;\">"
                        + "Result</td>\n");
                b.append(sp(in) + "<td class=\"head\" style=\"width: 20%;\">"
                        + "In v." + compareToVersion + "</td>\n");
            }
            else {
                b.append(sp(in) + "<td class=\"head\" style=\"width: 40%;\">"
                        + "Result</td>\n");
            }
            b.append(sp(--in) + "</tr>\n");
            b.append(sp(--in) + "</thead>\n");
            //----------------------------------------------------------------
            // Display all available results (and comparisons when requested)
            //----------------------------------------------------------------
            HashMap<String, Result> compResults = null;
            /*
             * If a comparison is requested, then retrieve the evaluation
             * results for the file (version) against which the current file
             * (version) results will be compared.
             */
            if ((doCompare) && (compareToFileId != null))
                compResults = selFile.getResults(mnemonics, compareToFileId);
            // Holds a map from metric mnemonic to metric description object
            HashMap<String, Metric> mnemToMetric =
                new HashMap<String, Metric>();
            // Display the evaluation results for the current file
            for (String mnemonic : mnemonics) {
                /*
                 * Get the description object of the metric that has evaluated
                 * the current result.
                 */
                Metric metric = null;
                if (mnemToMetric.containsKey(mnemonic)) {
                    metric = mnemToMetric.get(mnemonic);
                }
                else {
                    for (Metric nextMetric : project.retrieveMetrics())
                        if (nextMetric.getMnemonic().equals(mnemonic)) {
                            mnemToMetric.put(mnemonic, nextMetric);
                            metric = nextMetric;
                        }
                }
                // Display the metric statistic's row
                b.append(sp(in++) + "<tr>\n");
                b.append(sp(in) + "<td class=\"name\">"
                        + metric.getMnemonic()
                        + "</td>\n");
                b.append(sp(in) + "<td>"
                        + metric.getDescription()
                        + "</td>\n");
                b.append(sp(in) + "<td>"
                        + ((selFileResults.containsKey(mnemonic))
                                ? selFileResults.get(mnemonic).getString()
                                : "N/A")
                        + "</td>\n");
                // Display the comparison cell (if a comparison was requested)
                if ((doCompare) && (compareToFileId != null)) {
                    b.append(sp(in) + "<td>"
                            + ((compResults.containsKey(mnemonic))
                                    ? compResults.get(mnemonic).getString()
                                    : "N/A")
                            + "</td>\n");
                }
                b.append(sp(--in) + "</tr>\n");
            }
            b.append(sp(--in) + "</table>\n");
            b.append(sp(--in) + "</div>\n");
        }
        return b.toString();
    }

}
