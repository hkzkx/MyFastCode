package com.mmb.model.user;

import javax.validation.constraints.NotNull;

import com.mmb.annotation.Id;
import java.io.Serializable;


public class USER implements Serializable {

    private static final long serialVersionUID = 142043476265215214L;

	
	@Id
	private Long ID;
	
	private Long LOGINNAME;
	
	private Long MOBILEPHONE;
	
	private Long LOGINPASSWORD;
	
	private Long USERTYPE;
	
	private Long ROLE;
	
	private Long ISCREDITOR;
	
	private Long ISDEBTOR;
	
	private Long ISREPURCHASEPARTY;
	
	private Long ISGUARANTOR;
	
	private Long INVESTED;
	/**
	 * 0
	 */
	@NotNull(message="user.isfirstlogin.notnull")
	private Long ISFIRSTLOGIN;
	
	private Long LEVEL;
	/**
	 * NORMAL
	 */
	private Long USERSTATUS;
	
	private Long ISDELETED;
	
	private Long ISLOCKED;
	
	private Long ISTRADEPWDLOCKED;
	
	private Long CREATETIME;
	
	private Long LOCKEDTIME;
	
	private Long UPDATETIME;

	
	public void setID(Long ID){
		this.ID=ID;
	}
	public Long getID(){
		return this.ID;
	}
	public void setLOGINNAME(Long LOGINNAME){
		this.LOGINNAME=LOGINNAME;
	}
	public Long getLOGINNAME(){
		return this.LOGINNAME;
	}
	public void setMOBILEPHONE(Long MOBILEPHONE){
		this.MOBILEPHONE=MOBILEPHONE;
	}
	public Long getMOBILEPHONE(){
		return this.MOBILEPHONE;
	}
	public void setLOGINPASSWORD(Long LOGINPASSWORD){
		this.LOGINPASSWORD=LOGINPASSWORD;
	}
	public Long getLOGINPASSWORD(){
		return this.LOGINPASSWORD;
	}
	public void setUSERTYPE(Long USERTYPE){
		this.USERTYPE=USERTYPE;
	}
	public Long getUSERTYPE(){
		return this.USERTYPE;
	}
	public void setROLE(Long ROLE){
		this.ROLE=ROLE;
	}
	public Long getROLE(){
		return this.ROLE;
	}
	public void setISCREDITOR(Long ISCREDITOR){
		this.ISCREDITOR=ISCREDITOR;
	}
	public Long getISCREDITOR(){
		return this.ISCREDITOR;
	}
	public void setISDEBTOR(Long ISDEBTOR){
		this.ISDEBTOR=ISDEBTOR;
	}
	public Long getISDEBTOR(){
		return this.ISDEBTOR;
	}
	public void setISREPURCHASEPARTY(Long ISREPURCHASEPARTY){
		this.ISREPURCHASEPARTY=ISREPURCHASEPARTY;
	}
	public Long getISREPURCHASEPARTY(){
		return this.ISREPURCHASEPARTY;
	}
	public void setISGUARANTOR(Long ISGUARANTOR){
		this.ISGUARANTOR=ISGUARANTOR;
	}
	public Long getISGUARANTOR(){
		return this.ISGUARANTOR;
	}
	public void setINVESTED(Long INVESTED){
		this.INVESTED=INVESTED;
	}
	public Long getINVESTED(){
		return this.INVESTED;
	}
	public void setISFIRSTLOGIN(Long ISFIRSTLOGIN){
		this.ISFIRSTLOGIN=ISFIRSTLOGIN;
	}
	public Long getISFIRSTLOGIN(){
		return this.ISFIRSTLOGIN;
	}
	public void setLEVEL(Long LEVEL){
		this.LEVEL=LEVEL;
	}
	public Long getLEVEL(){
		return this.LEVEL;
	}
	public void setUSERSTATUS(Long USERSTATUS){
		this.USERSTATUS=USERSTATUS;
	}
	public Long getUSERSTATUS(){
		return this.USERSTATUS;
	}
	public void setISDELETED(Long ISDELETED){
		this.ISDELETED=ISDELETED;
	}
	public Long getISDELETED(){
		return this.ISDELETED;
	}
	public void setISLOCKED(Long ISLOCKED){
		this.ISLOCKED=ISLOCKED;
	}
	public Long getISLOCKED(){
		return this.ISLOCKED;
	}
	public void setISTRADEPWDLOCKED(Long ISTRADEPWDLOCKED){
		this.ISTRADEPWDLOCKED=ISTRADEPWDLOCKED;
	}
	public Long getISTRADEPWDLOCKED(){
		return this.ISTRADEPWDLOCKED;
	}
	public void setCREATETIME(Long CREATETIME){
		this.CREATETIME=CREATETIME;
	}
	public Long getCREATETIME(){
		return this.CREATETIME;
	}
	public void setLOCKEDTIME(Long LOCKEDTIME){
		this.LOCKEDTIME=LOCKEDTIME;
	}
	public Long getLOCKEDTIME(){
		return this.LOCKEDTIME;
	}
	public void setUPDATETIME(Long UPDATETIME){
		this.UPDATETIME=UPDATETIME;
	}
	public Long getUPDATETIME(){
		return this.UPDATETIME;
	}
}