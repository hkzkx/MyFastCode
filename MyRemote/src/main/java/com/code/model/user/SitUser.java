package com.code.model.user;

import javax.validation.constraints.NotNull;

import com.code.annotation.Id;
import java.io.Serializable;

/**
 * 网站系统：用户
 */
public class SitUser implements Serializable {

    private static final long serialVersionUID = 141984029722127495L;

	/**
	 * 会员id
	 */
	@Id
	private Long userId;
	
	private Long account;
	
	private Long password;
	/**
	 * 状态：0-会员未激活；1-激活成功；2-激活失败；
	 */
	@NotNull(message="situser.status.notnull")
	private Long status;
	
	@NotNull(message="situser.isdisable.notnull")
	private Long isDisable;
	/**
	 * 注册时会员类型：0-卖家；1-服务商；2-买家；3-货源商
            
	 */
	private Long registerMemberType;
	/**
	 * 是否有买家身份。卖家/服务商自动具备买家身份。
	 */
	@NotNull(message="situser.hasbuyeridentity.notnull")
	private Long hasBuyerIdentity;
	/**
	 * 是否有卖家身份。当通过卖家认证时，才有卖家身份。
	 */
	@NotNull(message="situser.hasselleridentity.notnull")
	private Long hasSellerIdentity;
	/**
	 * 是否有服务商身份。当通过服务商身认证时，才有服务商身份。
	 */
	@NotNull(message="situser.hasserviceridentity.notnull")
	private Long hasServicerIdentity;
	/**
	 * 是否有货源商身份
	 */
	@NotNull(message="situser.hassupplieridentity.notnull")
	private Long hasSupplierIdentity;
	/**
	 * 注册时用的邮箱，可以理解为企业邮箱。用于优安做邮件营销，必须做邮箱验证。
	 */
	private Long registerEmail;
	/**
	 * 注册时用的手机，可以理解为企业手机。用于优安做手机营销，必须做手机验证。
	 */
	private Long registerMobile;
	/**
	 * 邮箱激活码，只作为注册激活用。
	 */
	private Long emailActivateCode;
	/**
	 * 手机是否验证通过。
	 */
	@NotNull(message="situser.ismobileverify.notnull")
	private Long isMobileVerify;
	
	private Long createTime;
	
	private Long updateTime;

	
	public void setUserId(Long userId){
		this.userId=userId;
	}
	public Long getUserId(){
		return this.userId;
	}
	public void setAccount(Long account){
		this.account=account;
	}
	public Long getAccount(){
		return this.account;
	}
	public void setPassword(Long password){
		this.password=password;
	}
	public Long getPassword(){
		return this.password;
	}
	public void setStatus(Long status){
		this.status=status;
	}
	public Long getStatus(){
		return this.status;
	}
	public void setIsDisable(Long isDisable){
		this.isDisable=isDisable;
	}
	public Long getIsDisable(){
		return this.isDisable;
	}
	public void setRegisterMemberType(Long registerMemberType){
		this.registerMemberType=registerMemberType;
	}
	public Long getRegisterMemberType(){
		return this.registerMemberType;
	}
	public void setHasBuyerIdentity(Long hasBuyerIdentity){
		this.hasBuyerIdentity=hasBuyerIdentity;
	}
	public Long getHasBuyerIdentity(){
		return this.hasBuyerIdentity;
	}
	public void setHasSellerIdentity(Long hasSellerIdentity){
		this.hasSellerIdentity=hasSellerIdentity;
	}
	public Long getHasSellerIdentity(){
		return this.hasSellerIdentity;
	}
	public void setHasServicerIdentity(Long hasServicerIdentity){
		this.hasServicerIdentity=hasServicerIdentity;
	}
	public Long getHasServicerIdentity(){
		return this.hasServicerIdentity;
	}
	public void setHasSupplierIdentity(Long hasSupplierIdentity){
		this.hasSupplierIdentity=hasSupplierIdentity;
	}
	public Long getHasSupplierIdentity(){
		return this.hasSupplierIdentity;
	}
	public void setRegisterEmail(Long registerEmail){
		this.registerEmail=registerEmail;
	}
	public Long getRegisterEmail(){
		return this.registerEmail;
	}
	public void setRegisterMobile(Long registerMobile){
		this.registerMobile=registerMobile;
	}
	public Long getRegisterMobile(){
		return this.registerMobile;
	}
	public void setEmailActivateCode(Long emailActivateCode){
		this.emailActivateCode=emailActivateCode;
	}
	public Long getEmailActivateCode(){
		return this.emailActivateCode;
	}
	public void setIsMobileVerify(Long isMobileVerify){
		this.isMobileVerify=isMobileVerify;
	}
	public Long getIsMobileVerify(){
		return this.isMobileVerify;
	}
	public void setCreateTime(Long createTime){
		this.createTime=createTime;
	}
	public Long getCreateTime(){
		return this.createTime;
	}
	public void setUpdateTime(Long updateTime){
		this.updateTime=updateTime;
	}
	public Long getUpdateTime(){
		return this.updateTime;
	}
}