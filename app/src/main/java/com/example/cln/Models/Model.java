package com.example.cln.Models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Base class for both PointModel and MultiPointModel
 */
public abstract class Model {
    /**
     * id corresponding to the database. Object should be created AND associated with a map object
     * after the db insert is done.
     */
    private Long id;
    private String label;
    private Integer resourceId;
    private final String tableName;

    public Model(String label, Integer resourceId, String tableName) {
        this.label = label;
        this.resourceId = resourceId;
        this.tableName = tableName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public String getTableName() {
        return tableName;
    }

//    public abstract ContentValues getContentValues();
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Creates a JSONobject representation of the Model.
     * @return JSONObject
     */
    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("label", label);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return jsonObject;
    }

}
