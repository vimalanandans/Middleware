/**
 * @author Cory Henson
 */
package com.bezirk.aip;

import com.bezirk.middleware.messages.Event;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class Digest<A> extends Event {

    /**
     * UhU topic: answer
     */
    public static final String topic = "digest";

	
	/* Core Properties */

    /**
     * aip_version
     *
     * AiProtocol version: v0.1
     */
    public final String aip_version = "v0.1";

    /**
     * aip_id
     *
     * ID for the question. Used to map an answer with a question.
     */
    private String aip_id = null;

    /**
     * aip_subtopic
     *
     * Subtopic used by receiving services to know how to cast the event.
     */
    private String aip_subTopic = null;
	
	
	/* Digest Properties */

    /**
     * aip_summaries
     *
     * List of summaries (of type DigestItem).
     * NOTE: <A> represents DigestItem or a subclass that extends <A>.
     */
    private List<A> aip_summaries = null;
	
	
	/* Constructor */

    public Digest() {
        super(Flag.REPLY, topic);
    }
	
	
	/* Getter and setter methods */

    /**
     * Use instead of the generic Message.fromJson()
     * @param json
     * @return Digest
     */
    public static Digest<?> deserialize(String json) {
        return Event.fromJson(json, Digest.class);
    }

    public static <D, I> Digest<I> deserialize(String json, Class<D> dC, Class<I> iC) {
        Gson gson = new Gson();

        //******* UPDATE with the appropriate Answer class *******/
        @SuppressWarnings("unchecked")
        Digest<I> digest = (Digest<I>) gson.fromJson(json, dC);
        //********************************************************/

        // (1) Extract DigestItems from JSON
        // (2) Deserialize each DigestItem individually
        // (3) Add deserialized DigestItems to Digest
        final JsonObject jsonObj = new JsonParser().parse(json).getAsJsonObject();
        final JsonArray jsonObjSummaries = jsonObj.get("aip_summaries").getAsJsonArray();
        final List<I> summaries = new ArrayList<I>();

        for (int i = 0; i < jsonObjSummaries.size(); i++) {
            JsonObject jsonObjSummary = jsonObjSummaries.get(i).getAsJsonObject();
            I digestItem = gson.fromJson(jsonObjSummary.toString(), iC);
            summaries.add(digestItem);
        }

        digest.setSummaries(summaries);
        return digest;
    }

    public String getId() {
        return aip_id;
    }

    public void setId(String id) {
        this.aip_id = id;
    }

    public List<A> getSummaries() {
        return aip_summaries;
    }

    public void setSummaries(List<A> summaries) {
        this.aip_summaries = summaries;
    }

    public String getSubTopic() {
        return aip_subTopic;
    }

    public void setSubTopic(String subTopic) {
        this.aip_subTopic = subTopic;
    }
}