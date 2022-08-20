package com.example.demo.base;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

/**
 * Basisklasse für alle Entitäten
 * @author udobischof
 *
 */
@MappedSuperclass
public abstract class AbstractEntity extends ValidateableVO {

	private static final long serialVersionUID = -2166890078905159199L;
	
	public static final String ID = "id";
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	protected Long id;
	
	public Long getId() {
		return id;
	}
	
	public static final String MODSTAMP = "modStamp";
    @Column(name="modStamp", nullable=false, columnDefinition="timestamp with time zone not null")
    @Temporal(TemporalType.TIMESTAMP)
    @Version
	protected Date modStamp;
	
	public static final String CREATESTAMP = "createStamp";
    @Column(name="createStamp", length=29, nullable=false, columnDefinition="timestamp with time zone not null")
    @Temporal(TemporalType.TIMESTAMP)
	protected Date createStamp;

	@PrePersist
	public void onPrePersist() {
		this.setCreateStamp(new Date());
		this.setModStamp(new Date());
	}
	
	@PreUpdate
	public void onPreUpdate() {
		this.setModStamp(new Date());
	}

    public Date getCreateStamp() {
        return this.createStamp;
    }
    
    public void setCreateStamp(Date createStamp) {
    	this.createStamp = createStamp;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public Date getModStamp() {
        return this.modStamp;
    }

    public void setModStamp(Date modStamp) {
    	this.modStamp = modStamp;
    }
    
}

