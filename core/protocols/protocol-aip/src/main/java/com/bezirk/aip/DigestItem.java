/**
 * DigestItem: member of summary list, contained in Digest message
 *
 * @author Cory Henson
 * @modified 06/11/2014
 */
package com.bezirk.aip;

import java.util.List;

public class DigestItem {

	/* DigestItem Properties */

    /**
     * aip_title
     *
     * Title of the summary.
     */
    private String aip_title = null;

    /**
     * aip_summary
     *
     * Summary of the answer to the question.
     */
    private String aip_summary = null;

    /**
     * aip_link
     *
     * A URI for the document that provides the more complete answer to the question.
     */
    private String aip_link = null;

    /**
     * aip_image
     *
     * An image associated with this summary (URI of the image).
     */
    private String aip_image = null;

    /**
     * aip_confidence
     *
     * Measure of confidence that answer is correct (and/or applicable).
     * Value should be between 0.0 and 1.0.
     * Default value: 0.0
     */
    private double aip_confidence = 0.0;

    /**
     * aip_about
     *
     * About specifies information related to the semantics of the question,
     * or what the question is about. In the Answer message, it specifically
     * relates to what the question was interpreted to be about, if not provided
     * by the Question message, in order to produce this answer.
     */
    private List<String> aip_about = null;

    /**
     * aip_source
     *
     * Source specifies the provenance of the answer (where the answer came from).
     */
    private String aip_source = null;
	
	
	/* Context Property */

    /**
     * aip_context
     *
     * Context specifies information that was used to generate an answer,
     * which is related to the circumstances in which the question was asked.
     * In the AnswerQuestion message, it specifically relates to context
     * used to produce this answer.
     */
    private Context aip_context = null;


    //*  Getter and setter methods */

    public String getTitle() {
        return aip_title;
    }

    public void setTitle(String title) {
        this.aip_title = title;
    }

    public String getSummary() {
        return aip_summary;
    }

    public void setSummary(String summary) {
        this.aip_summary = summary;
    }

    public String getLink() {
        return aip_link;
    }

    public void setLink(String link) {
        this.aip_link = link;
    }

    public String getImage() {
        return aip_image;
    }

    public void setImage(String image) {
        this.aip_image = image;
    }

    public double getConfidence() {
        return aip_confidence;
    }

    public void setConfidence(double confidence) {
        this.aip_confidence = confidence;
    }

    public List<String> getAbout() {
        return aip_about;
    }

    public void setAbout(List<String> about) {
        this.aip_about = about;
    }

    public Context getContext() {
        return aip_context;
    }

    public void setContext(Context context) {
        this.aip_context = context;
    }

    public String getSource() {
        return aip_source;
    }

    public void setSource(String source) {
        this.aip_source = source;
    }
}