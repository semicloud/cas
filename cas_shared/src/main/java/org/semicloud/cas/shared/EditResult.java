package org.semicloud.cas.shared;

import org.semicloud.cas.shared.al.SharedDal;

import java.util.ArrayList;
import java.util.List;

/**
 * 人工编辑结果
 */
public class EditResult {

    /**
     * EQID
     */
    private String _eqID;

    /**
     * 旧TaskID
     */
    private String _oldTaskID;

    /**
     * 新TaskID
     */
    private String _taskID;

    /**
     * 编辑区域列表
     */
    private List<EditRegion> _regions;

    /**
     * Instantiates a new edits the result.
     */
    public EditResult() {
        _regions = new ArrayList<EditRegion>();
    }

    /**
     * 获得最新的人工编辑结果
     *
     * @return the latest
     */
    public static EditResult getLatest() {
        return SharedDal.getLatestEditResult();
    }

    /**
     * Gets the eq id.
     *
     * @return the eq id
     */
    public String getEqID() {
        return _eqID;
    }

    /**
     * Sets the eq id.
     *
     * @param eqID the new eq id
     */
    public void setEqID(String eqID) {
        _eqID = eqID;
    }

    /**
     * Gets the old task id.
     *
     * @return the old task id
     */
    public String getOldTaskID() {
        return _oldTaskID;
    }

    /**
     * Sets the old task id.
     *
     * @param oldTaskID the new old task id
     */
    public void setOldTaskID(String oldTaskID) {
        this._oldTaskID = oldTaskID;
    }

    /**
     * Gets the task id.
     *
     * @return the task id
     */
    public String getTaskID() {
        return _taskID;
    }

    /**
     * Sets the task id.
     *
     * @param taskID the new task id
     */
    public void setTaskID(String taskID) {
        _taskID = taskID;
    }

    /**
     * Gets the regions.
     *
     * @return the regions
     */
    public List<EditRegion> getRegions() {
        return _regions;
    }

    /**
     * Adds the region.
     *
     * @param item the item
     */
    public void addRegion(EditRegion item) {
        _regions.add(item);
    }

    /**
     * 更新处理状态
     *
     * @return true, if successful
     */
    public boolean updateProcessState() {
        return SharedDal.markEventIsProcessTrue(this);
    }
}
