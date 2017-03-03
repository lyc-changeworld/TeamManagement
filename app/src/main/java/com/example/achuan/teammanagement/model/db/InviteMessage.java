package com.example.achuan.teammanagement.model.db;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

/**
 * Created by achuan on 17-3-2.
 * 功能：创建表(使用的LitePal开源数据库)
 *      申请消息的数据库表结构
 */

public class InviteMessage extends DataSupport{

	//id号
	private int id;
	//申请人名称
	private String from;
	//申请时间
	private long time;
	//添加理由
	private String reason;


	/***需要特别注意:
	 * 这里的InviteMesageStatus无法存储到表中,存储的应该是对应枚举类中的位置
	 * ,所以需要忽略掉这一项***/
	@Column(ignore = true)
	private InviteMesageStatus status;//未验证，已同意等状态
	private int statusOrdinal;//代表状态在枚举类中的序数

	public int getStatusOrdinal() {
		return statusOrdinal;
	}

	public void setStatusOrdinal(int statusOrdinal) {
		this.statusOrdinal = statusOrdinal;
	}

	//群id
	private String groupId;
	//群名称
	private String groupName;
	//群邀请者
	private String groupInviter;



	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	/*由于InviteMesageStatus无法存储到数据中,所以下面的方法最好屏蔽不用,避免使用错误*/
	/*public InviteMesageStatus getStatus() {
		return status;
	}

	public void setStatus(InviteMesageStatus status) {
		this.status = status;
	}*/

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupInviter() {
		return groupInviter;
	}

	public void setGroupInviter(String groupInviter) {
		this.groupInviter = groupInviter;
	}

	public enum InviteMesageStatus{
	    //==好友
		/**被邀请*/
		BEINVITEED,
		/**被拒绝*/
		BEREFUSED,
		/**对方同意*/
		BEAGREED,
		
		//==群组
		/**对方申请进入群*/
		BEAPPLYED,
		/**我同意了对方的请求*/
		AGREED,
		/**我拒绝了对方的请求*/
		REFUSED,
		
		//==群邀请
		/**收到对方的群邀请**/
		GROUPINVITATION,
		/**收到对方同意群邀请的通知**/
		GROUPINVITATION_ACCEPTED,
        /**收到对方拒绝群邀请的通知**/
		GROUPINVITATION_DECLINED;

		/*参考链接：
		* http://blog.csdn.net/brooksychen/article/details/3342446*/

		/*自定义可以通过序数获取对应值的方法*/
		public static InviteMesageStatus valueOf(int ordinal) {
			if (ordinal < 0 || ordinal >= values().length) {
				throw new IndexOutOfBoundsException("Invalid ordinal");
			}
			return values()[ordinal];
		}
	}
	
}



