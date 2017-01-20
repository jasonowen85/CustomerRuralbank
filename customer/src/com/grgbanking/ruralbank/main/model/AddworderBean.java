package com.grgbanking.ruralbank.main.model;

import java.io.Serializable;

/**
 * Created by Aaron on 2016/8/9.
 */
public class AddworderBean implements Serializable {

     String supplier;
     String supplierId;

    String equipment;
    String equipmentId;
    String model;
    String modelId;

    public void setModel(String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public String getModelId() {
        return modelId;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getSupplier() {
        return supplier;
    }


}
