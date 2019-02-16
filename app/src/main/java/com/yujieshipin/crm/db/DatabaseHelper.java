package com.yujieshipin.crm.db;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
//import com.yujieshipin.crm.entity.StudentScoreItem;
//import com.yujieshipin.crm.entity.StudentTestItem;
import com.yujieshipin.crm.entity.AccountInfo;
import com.yujieshipin.crm.entity.AlbumMsgInfo;
import com.yujieshipin.crm.entity.AttendanceOfStudent;
import com.yujieshipin.crm.entity.ChatFriend;
import com.yujieshipin.crm.entity.ChatMsg;
import com.yujieshipin.crm.entity.ChatMsgDetail;
import com.yujieshipin.crm.entity.ContactsFriends;
import com.yujieshipin.crm.entity.ContactsGroup;
import com.yujieshipin.crm.entity.ContactsMember;
import com.yujieshipin.crm.entity.ContactsMemberTeacher;
import com.yujieshipin.crm.entity.Dictionary;
import com.yujieshipin.crm.entity.DisciplineScore;
import com.yujieshipin.crm.entity.DownloadInfo;
import com.yujieshipin.crm.entity.DownloadSubject;
import com.yujieshipin.crm.entity.Equipment;
import com.yujieshipin.crm.entity.MyClassSchedule;
import com.yujieshipin.crm.entity.Notice;
import com.yujieshipin.crm.entity.NoticeClass;
import com.yujieshipin.crm.entity.QueryTheMarkOfStudent;
import com.yujieshipin.crm.entity.Schedule;
import com.yujieshipin.crm.entity.StatisticsScoreOfStudents;
import com.yujieshipin.crm.entity.Student;
import com.yujieshipin.crm.entity.StudentAttence;
import com.yujieshipin.crm.entity.StudentPic;
import com.yujieshipin.crm.entity.StudentScore;
import com.yujieshipin.crm.entity.StudentTest;
import com.yujieshipin.crm.entity.Suggestions;
import com.yujieshipin.crm.entity.TeacherInfo;
import com.yujieshipin.crm.entity.TestEntity;
import com.yujieshipin.crm.entity.TestStartEntity;
import com.yujieshipin.crm.entity.TestStatusEntity;
import com.yujieshipin.crm.entity.User;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
	private String TAG = "DatabaseHelper";
	private Dao<Equipment, Integer> eqmDao = null;
	private Dao<User, Integer> userDao = null;
	
	Dao<NoticeClass, Integer> noticeDao=null;
	private Dao<AttendanceOfStudent, Integer> attendtanceDao = null;
	private Dao<DownloadSubject, Integer> downloadSubjectDao = null;
	private Dao<DisciplineScore, Integer> disciplineScoreDao = null;
	private Dao<TestEntity, Integer> testentityDao = null;
	private Dao<QueryTheMarkOfStudent, Integer> queryTheMarkOfStudentDao = null;
	private Dao<TeacherInfo, Integer> teacherinfoDao = null;
	private Dao<MyClassSchedule, Integer> myClassScheduleDao = null;
	
	private Dao<StatisticsScoreOfStudents, Integer> statisticsScoreOfStudentsDao = null;
	private Dao<Schedule, Integer> scheduleDao = null;
	private Dao<TestStartEntity,Integer> startTestDao = null;
	private static final String DATABASE_NAME = "orm136.db";

	private static final int DATABASE_VERSION = 1;
	private Dao<StudentAttence, Integer> studentAttenceDao = null;
	private Dao<Dictionary, Integer> dictionaryDao = null;
	private Dao<StudentScore, Integer> studentScoreDao = null;

	private Dao<StudentTest, Integer> studentTestDao = null;

	private Dao<StudentPic,Integer> studentPicDao = null;
	private Dao<Suggestions,Integer> suggestionsDao = null;
	private Dao<ChatMsg,Integer> chatMsgDao = null;
	private Dao<ChatMsgDetail,Integer> chatMsgDetailDao = null;
	private Dao<ChatFriend,Integer> chatFriendDao = null;
	private Dao<AlbumMsgInfo,Integer> albumMsgDao=null;
	private Dao<TestStatusEntity, Integer> testStatusDao = null;
	private Dao<DownloadInfo, Integer> downloadInfoDao = null;
	private Dao<ContactsMember, Integer> ContactsMemberDao = null;
	private Dao<AccountInfo, Integer> accountInfoDao = null;
	private Dao<Notice, Integer> noticeInfoDao = null;
	private Dao<ContactsFriends, Integer> contactsFriendsDao = null;
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		Log.d(TAG, "-------------------->进来啦1");
		
	}
	@Override
	public void onCreate(SQLiteDatabase database,
			ConnectionSource connectionsSourse) {
		Log.d(TAG, "----------onCreate------");
		this.dropTable(connectionsSourse, 0, 0);
		this.createTable(connectionsSourse);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase database,
			ConnectionSource connectionSourse, int oldVer, int newVer) {
		
	}

	
	/**
	 * 功能描述：创建表
	 * 
	 * @author zhuliang 2013-11-29 下午2:30:00
	 * 
	 * @param connectionsSourse
	 */
	public void createTable(ConnectionSource connectionsSourse) {
		try {
			TableUtils.createTable(connectionsSourse, DisciplineScore.class);
			TableUtils.createTable(connectionsSourse, NoticeClass.class);
			TableUtils.createTable(connectionsSourse,
					StatisticsScoreOfStudents.class);
			TableUtils.createTable(connectionsSourse,
					QueryTheMarkOfStudent.class);
			TableUtils
					.createTable(connectionsSourse, AttendanceOfStudent.class);
			TableUtils.createTable(connectionsSourse, DownloadSubject.class);
			TableUtils.createTable(connectionsSourse, TestEntity.class);
			TableUtils.createTable(connectionsSourse, Equipment.class);
			TableUtils.createTable(connectionsSourse, User.class);
			TableUtils.createTable(connectionsSourse, Student.class);
			TableUtils.createTable(connectionsSourse, Schedule.class);
			TableUtils.createTable(connectionsSourse, TeacherInfo.class);
			TableUtils.createTable(connectionsSourse, StudentAttence.class);
			TableUtils.createTable(connectionsSourse, Dictionary.class);
			TableUtils.createTable(connectionsSourse, StudentScore.class);
//			TableUtils.createTable(connectionsSourse, StudentScoreItem.class);
			TableUtils.createTable(connectionsSourse, StudentTest.class);
//			TableUtils.createTable(connectionsSourse, StudentTestItem.class);
			TableUtils.createTable(connectionsSourse, TestStartEntity.class);
			TableUtils.createTable(connectionsSourse, ContactsMember.class);
			TableUtils.createTable(connectionsSourse, ContactsMemberTeacher.class);
			TableUtils.createTable(connectionsSourse, ContactsGroup.class);
			TableUtils.createTable(connectionsSourse, ContactsFriends.class);
			TableUtils.createTable(connectionsSourse, StudentPic.class);
			TableUtils.createTable(connectionsSourse, Suggestions.class);
			TableUtils.createTable(connectionsSourse, ChatMsg.class);
			TableUtils.createTable(connectionsSourse, ChatMsgDetail.class);
			TableUtils.createTable(connectionsSourse, ChatFriend.class);
			TableUtils.createTable(connectionsSourse, TestStatusEntity.class);
			TableUtils.createTable(connectionsSourse, DownloadInfo.class);
			TableUtils.createTable(connectionsSourse, AccountInfo.class);
			TableUtils.createTable(connectionsSourse, AlbumMsgInfo.class);
			TableUtils.createTable(connectionsSourse, Notice.class);
			
		} catch (SQLException e) {
			Log.e("DatabaseHelper", "DatabaseHelper创表失敗！");
			
		}

	}

	public void dropTable(ConnectionSource connectionSourse, int oldVer,
			int newVer) {
		try {
			TableUtils.dropTable(connectionSourse, NoticeClass.class, true);
			TableUtils.dropTable(connectionSourse, DisciplineScore.class, true);
			TableUtils.dropTable(connectionSourse,
					StatisticsScoreOfStudents.class, true);
			TableUtils.dropTable(connectionSourse, QueryTheMarkOfStudent.class,
					true);
			TableUtils.dropTable(connectionSourse, AttendanceOfStudent.class,
					true);
			TableUtils.dropTable(connectionSourse, DownloadSubject.class, true);
			TableUtils.dropTable(connectionSourse, TestEntity.class, true);
			TableUtils.dropTable(connectionSourse, Schedule.class, true);
			TableUtils.dropTable(connectionSourse, TeacherInfo.class, true);
			TableUtils.dropTable(connectionSourse, Equipment.class, true);  
			TableUtils.dropTable(connectionSourse, User.class, true);
			TableUtils.dropTable(connectionSourse, Student.class, true);
//			TableUtils.dropTable(connectionSourse, Content.class, true);
			
			TableUtils.dropTable(connectionSourse, TestStartEntity.class, true);
			TableUtils.dropTable(connectionSourse, ContactsMember.class, true);
			TableUtils.dropTable(connectionSourse, ContactsMemberTeacher.class, true);
			TableUtils.dropTable(connectionSourse, ContactsGroup.class, true);
			TableUtils.dropTable(connectionSourse, ContactsFriends.class, true);
			TableUtils.dropTable(connectionSourse, StudentPic.class, true);
			TableUtils.dropTable(connectionSourse, Suggestions.class, true);
			TableUtils.dropTable(connectionSourse, ChatMsg.class, true);
			TableUtils.dropTable(connectionSourse, ChatMsgDetail.class, true);
			TableUtils.dropTable(connectionSourse, ChatFriend.class, true);
			TableUtils.dropTable(connectionSourse, TestStatusEntity.class, true);
			TableUtils.dropTable(connectionSourse, DownloadInfo.class, true);
			TableUtils.dropTable(connectionSourse, AccountInfo.class, true);
			TableUtils.dropTable(connectionSourse, AlbumMsgInfo.class,true);
			TableUtils.dropTable(connectionSourse, Notice.class, true);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(),
					"Unable to upgrade database from version " + oldVer
							+ " to new " + newVer, e);
		}
	}

	public Dao<DisciplineScore, Integer> getDisciplineScoreDao()
			throws SQLException {
		if (disciplineScoreDao == null) {
			disciplineScoreDao = getDao(DisciplineScore.class);
		}
		return disciplineScoreDao;
	}
	public Dao<NoticeClass, Integer> getNoticeClassDao()
			throws SQLException {
		if (noticeDao == null) {
			noticeDao = getDao(NoticeClass.class);
		}
		return noticeDao;
	}

	public Dao<QueryTheMarkOfStudent, Integer> getQueryTheMarkOfStudentDao()
			throws SQLException {
		if (queryTheMarkOfStudentDao == null) {
			queryTheMarkOfStudentDao = getDao(QueryTheMarkOfStudent.class);
		}
		return queryTheMarkOfStudentDao;
	}

	public Dao<StatisticsScoreOfStudents, Integer> getStatisticsScoreOfStudentsDao()
			throws SQLException {
		if (statisticsScoreOfStudentsDao == null) {
			statisticsScoreOfStudentsDao = getDao(StatisticsScoreOfStudents.class);
		}
		return statisticsScoreOfStudentsDao;
	}
	public Dao<Equipment, Integer> getEqmDao() throws SQLException {
		if (eqmDao == null) {
			eqmDao = getDao(Equipment.class);
		}
		return eqmDao;
	}
	
	public Dao<User, Integer> getUserDao() throws SQLException {
		if (userDao == null) {
			userDao = getDao(User.class);
		}
		return userDao;
	}

	

	public Dao<Schedule, Integer> getScheduleDao() throws SQLException {
		if (scheduleDao == null) {
			scheduleDao = getDao(Schedule.class);
		}
		return scheduleDao;
	}

	public Dao<TeacherInfo, Integer> getTeacherInfoDao() throws SQLException {
		if (teacherinfoDao == null) {
			teacherinfoDao = getDao(TeacherInfo.class);
		}
		return teacherinfoDao;
	}
	public Dao<MyClassSchedule, Integer> getMyClassScheduleDao() throws SQLException {
		if (myClassScheduleDao == null) {
			myClassScheduleDao = getDao(MyClassSchedule.class);
			if(!myClassScheduleDao.isTableExists())
				TableUtils.createTable(this.getConnectionSource(), MyClassSchedule.class);
		}
		return myClassScheduleDao;
	}
	public Dao<TestEntity, Integer> getTestEntityDao() throws SQLException {
		if (testentityDao == null) {
			testentityDao = getDao(TestEntity.class);
		}
		return testentityDao;
	}

	public Dao<DownloadSubject, Integer> getDownloadSubjectDao()
			throws SQLException {
		if (downloadSubjectDao == null) {
			downloadSubjectDao = getDao(DownloadSubject.class);
		}
		return downloadSubjectDao;
	}

	public Dao<AttendanceOfStudent, Integer> geAttendanceOfStudentDao()
			throws SQLException {
		if (attendtanceDao == null) {
			attendtanceDao = getDao(AttendanceOfStudent.class);
		}
		return attendtanceDao;
	}

//	public Dao<Content, Integer> getContentDao() throws SQLException {
//		if (contentDao == null) {
//			contentDao = getDao(Content.class);
//		}
//		return contentDao;
//	}
	
	public Dao<StudentAttence, Integer> getStudentAttenceDao() 
			throws SQLException {
		if (studentAttenceDao == null) {
			studentAttenceDao = getDao(StudentAttence.class);
		}
		return studentAttenceDao;
	}

	public Dao<Dictionary, Integer> getDictionaryDao() 
			throws SQLException {
		if (dictionaryDao == null) {
			dictionaryDao = getDao(Dictionary.class);
		}
		return dictionaryDao;
	}

	public Dao<StudentScore, Integer> getStudentScoreDao() 
			throws SQLException {
		if (studentScoreDao == null) {
			studentScoreDao = getDao(StudentScore.class);
		}
		return studentScoreDao;
	}

//	public Dao<StudentScoreItem, Integer> getStudentScoreItemDao() 
//			throws SQLException {
//		if (studentScoreItemDao == null) {
//			studentScoreItemDao = getDao(StudentScoreItem.class);
//		}
//		return studentScoreItemDao;
//	}

	public Dao<StudentTest, Integer> getStudentTestDao() 
			throws SQLException {
		if (studentTestDao == null) {
			studentTestDao = getDao(StudentTest.class);
		}
		return studentTestDao;
	}

//	public Dao<StudentTestItem, Integer> getStudentTestItemDao() 
//			throws SQLException {
//		if (studentTestItemDao == null) {
//			studentTestItemDao = getDao(StudentTestItem.class);
//		}
//		return studentTestItemDao;
//	}
	
	public Dao<TestStartEntity, Integer> getStartTestDao() throws SQLException{
		if(startTestDao == null){
			startTestDao = getDao(TestStartEntity.class);
		}
		return startTestDao;
	}

	public Dao<StudentPic, Integer> getStudentPicDao() throws SQLException {
		if(studentPicDao == null){
			studentPicDao = getDao(StudentPic.class);
		}
		return studentPicDao;
	}
	

	
	public Dao<Suggestions, Integer> getSuggestionsDao() throws SQLException{
		if(suggestionsDao == null){
			suggestionsDao = getDao(Suggestions.class);
		}
		return suggestionsDao;
	}

	public Dao<ChatMsg, Integer> getChatMsgDao() throws SQLException {
		if(chatMsgDao == null){
			chatMsgDao = getDao(ChatMsg.class);
		}
		return chatMsgDao;
	}
	public Dao<ChatMsgDetail, Integer> getChatMsgDetailDao() throws SQLException {
		if(chatMsgDetailDao == null){
			chatMsgDetailDao = getDao(ChatMsgDetail.class);
		}
		if(!chatMsgDetailDao.isTableExists())
			TableUtils.createTable(this.getConnectionSource(), ChatMsgDetail.class);
		return chatMsgDetailDao;
	}

	public Dao<ChatFriend, Integer> getChatFriendDao() throws SQLException {
		if(chatFriendDao == null){
			chatFriendDao = getDao(ChatFriend.class);
		}
		return chatFriendDao;
	}

	public Dao<AlbumMsgInfo, Integer> getAlbumMsgDao() throws SQLException {
		if(albumMsgDao == null){
			albumMsgDao = getDao(AlbumMsgInfo.class);
		}
		if(!albumMsgDao.isTableExists())
			TableUtils.createTable(this.getConnectionSource(), AlbumMsgInfo.class);
		return albumMsgDao;
	}

	public Dao<TestStatusEntity, Integer> getTestStatusDao() throws SQLException {
		if(testStatusDao == null){
			testStatusDao = getDao(TestStatusEntity.class);
		}
		return testStatusDao;
	}
	public Dao<DownloadInfo, Integer> getDownloadInfoDao() throws SQLException {
		if(downloadInfoDao == null){
			downloadInfoDao = getDao(DownloadInfo.class);
		}
		return downloadInfoDao;
	}
	public Dao<ContactsMember, Integer> getContactsMemberDao() throws SQLException {
		if(ContactsMemberDao == null){
			ContactsMemberDao = getDao(ContactsMember.class);
		}
		if(!ContactsMemberDao.isTableExists())
			TableUtils.createTable(this.getConnectionSource(), ContactsMember.class);
		return ContactsMemberDao;
	}
	
	public Dao<AccountInfo, Integer> getAccountInfoDao() throws SQLException {
		if(accountInfoDao == null){
			accountInfoDao = getDao(AccountInfo.class);
		}
		return accountInfoDao;
	}
	public Dao<ContactsFriends, Integer> getContactsFriendsDao() throws SQLException {
		if(contactsFriendsDao == null){
			contactsFriendsDao = getDao(ContactsFriends.class);
		}
		if(!contactsFriendsDao.isTableExists())
			TableUtils.createTable(this.getConnectionSource(), ContactsFriends.class);
		return contactsFriendsDao;
	}
	
	public Dao<Notice, Integer> getNoticeInfoDao() throws SQLException {
		if(noticeInfoDao == null){
			noticeInfoDao = getDao(Notice.class);
		}
		if(!noticeInfoDao.isTableExists())
			TableUtils.createTable(this.getConnectionSource(), Notice.class);
		return noticeInfoDao;
	}
//	public Dao<DownloadInfo, Integer> getDownloadInfoDao() throws SQLException{
//		if(downloadInfoDao == null){
//			downloadInfoDao = getDao(DownloadInfo.class);
//		}
//		return downloadInfoDao;
//	}
	
	
	// @Override
	// public void close() {
	// super.close();
	// userDao=null;
	// }
}
