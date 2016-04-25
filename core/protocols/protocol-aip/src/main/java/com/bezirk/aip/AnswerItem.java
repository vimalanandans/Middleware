/**
 * AnswerItem
 *
 * @author Cory Henson
 * @modified 06/09/2014
 * @specification https://fe0vmc0345.de.bosch.com/wiki/pages/viewpage.action?pageId=24117500#AiPQ&ASpecification-AnswerItem
 */
package com.bezirk.aip;

import java.util.List;

public class AnswerItem {

	/* AnswerItem Properties */

    /**
     * aip_answer
     *
     * Answer to the question.
     */
    private String aip_answer = null;

    /**
     * aip_format
     *
     * Data format of answer (used to fromJSON or cast answer)
     */
    private String aip_format = null;

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

    public String getAnswer() {
        return aip_answer;
    }

    public void setAnswer(String answer) {
        this.aip_answer = answer;
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

    public String getFormat() {
        return aip_format;
    }

    public void setFormat(String format) {
        this.aip_format = format;
    }
}
