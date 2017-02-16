package com.yujieshipin.crm.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.PreparedUpdate;
import com.yujieshipin.crm.entity.DownloadInfo;

/**
 * 
 * 一个业务类
 */
public class DownloaderDao {
	private DatabaseHelper database;
	private Dao<DownloadInfo, Integer> downloadInfoDao;
	private Context context;
	public DownloaderDao(Context context) {
		this.context = context;
		database = OpenHelperManager.getHelper(context, DatabaseHelper.class);
		try {
			downloadInfoDao = database.getDownloadInfoDao();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 查看数据库中是否有数据
	 */
	public boolean isHasInfors(String urlstr) {
		
//		SQLiteDatabase database = database.getReadableDatabase();
//		String sql = "select count(*)  from download_info where url=?";
//		Cursor cursor = database.rawQuery(sql, new String[] { urlstr });
//		cursor.moveToFirst();
//		int count = cursor.getInt(0);
//		cursor.close();
		try {
			int count = (int) downloadInfoDao.queryBuilder().where().eq("url", urlstr).countOf();
			return count == 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 保存 下载的具体信息
	 */
	public void saveInfos(List<DownloadInfo> infos) {
//		SQLiteDatabase database = dbHelper.getWritableDatabase();
		for (DownloadInfo info : infos) {
//			String sql = "insert into download_info(thread_id,start_pos, end_pos,compelete_size,url) values (?,?,?,?,?)";
//			Object[] bindArgs = { info.getThreadId(), info.getStartPos(),
//					info.getEndPos(), info.getCompeleteSize(), info.getUrl() };
//			database.execSQL(sql, bindArgs);
			try {
				downloadInfoDao.create(info);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 得到下载具体信息
	 */
	public List<DownloadInfo> getInfos(String urlstr) {
		List<DownloadInfo> list = new ArrayList<DownloadInfo>();
//		SQLiteDatabase database = dbHelper.getReadableDatabase();
//		String sql = "select thread_id, start_pos, end_pos,compelete_size,url from download_info where url=?";
//		Cursor cursor = database.rawQuery(sql, new String[] { urlstr });
//		while (cursor.moveToNext()) {
//			DownloadInfo info = new DownloadInfo(cursor.getInt(0),
//					cursor.getInt(1), cursor.getInt(2), cursor.getInt(3),
//					cursor.getString(4));
//			list.add(info);
//		}
//		cursor.close();
		try {
			list = downloadInfoDao.queryBuilder().where().eq("url", urlstr).query();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 更新数据库中的下载信息
	 */
	public void updataInfos(int threadId, int compeleteSize, String urlstr) {
//		SQLiteDatabase database = dbHelper.getReadableDatabase();
//		String sql = "update download_info set compelete_size=? where thread_id=? and url=?";
//		Object[] bindArgs = { compeleteSize, threadId, urlstr };
//		database.execSQL(sql, bindArgs);
		try {
//			DownloadInfo downloadInfo = downloadInfoDao.queryBuilder().where().eq("threadId", threadId).and().eq("url", urlstr).queryForFirst();
//			if (downloadInfo != null) {
//				downloadInfo.setCompeleteSize(compeleteSize);
//				downloadInfoDao.update(downloadInfo);
//			}
			PreparedUpdate<DownloadInfo> preparedUpdate = (PreparedUpdate<DownloadInfo>) downloadInfoDao.updateBuilder()
					.updateColumnValue("compeleteSize", compeleteSize)
					.where().eq("threadId", threadId).and().eq("url", urlstr).prepare();
			downloadInfoDao.update(preparedUpdate);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 关闭数据库
	 */
//	public void closeDb() {
//		dbHelper.close();
//	}

	/**
	 * 下载完成后删除数据库中的数据
	 */
	public void delete(String url) {
//		SQLiteDatabase database = dbHelper.getReadableDatabase();
//		database.delete("download_info", "url=?", new String[] { url });
//		database.close();
		try {
			downloadInfoDao.delete((PreparedDelete<DownloadInfo>)downloadInfoDao.deleteBuilder().where().eq("url", url).prepare());
//			Toast.makeText(context, "-----:"+downloadInfoDao.queryBuilder().where().eq("url", url).countOf(), 0).show();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
