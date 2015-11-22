package pl.p.lodz.iis.hr.services;

import pl.p.lodz.iis.hr.configuration.Long2;

import java.io.Serializable;

public class GHReviewAddForm implements Serializable {

    private static final long serialVersionUID = 7269786683104395211L;

    private String name;
    private Long2 respPerPeer;
    private Long2 courseID;
    private Long2 formID;
    private String repositoryFullName;
    private Long2 ignoreWarning;

    public String getName() {
        return (name != null) ? name : "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long2 getRespPerPeer() {
        return respPerPeer;
    }

    public long getRespPerPeerLong() {
        return (respPerPeer != null) ? respPerPeer.get() : new Long2().get();
    }

    public void setRespPerPeer(Long2 respPerPeer) {
        this.respPerPeer = respPerPeer;
    }

    public Long2 getCourseID() {
        return courseID;
    }

    public long getCourseIDLong() {
        return (courseID != null) ? courseID.get() : new Long2().get();
    }

    public void setCourseID(Long2 courseID) {
        this.courseID = courseID;
    }

    public Long2 getFormID() {
        return formID;
    }

    public long getFormIDLong() {
        return (formID != null) ? formID.get() : new Long2().get();
    }

    public void setFormID(Long2 formID) {
        this.formID = formID;
    }

    public String getRepositoryFullName() {
        return (repositoryFullName != null) ? repositoryFullName : "";
    }

    public void setRepositoryFullName(String repositoryFullName) {
        this.repositoryFullName = repositoryFullName;
    }

    public long getIgnoreWarning() {
        return (ignoreWarning != null) ? ignoreWarning.get() : new Long2().get();
    }

    public void setIgnoreWarning(Long2 ignoreWarning) {
        this.ignoreWarning = ignoreWarning;
    }
}
