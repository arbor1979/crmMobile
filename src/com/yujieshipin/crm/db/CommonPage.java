package com.yujieshipin.crm.db;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.yujieshipin.crm.entity.Page;
import com.yujieshipin.crm.util.AppUtility;



public class CommonPage {
//	private PreparedQuery preparedQuery;
//	private Dao dao;
	public CommonPage(){
		
	}
	
	public Page queryPageStartBegin(Page page,Dao dao,String columnNames,Object[] columnValues,
			String orderbyColumn,String orderbyValue){
		boolean order = true;
		QueryBuilder queryBuilder = dao.queryBuilder();
		QueryBuilder queryBuilderTotal = dao.queryBuilder();
		if (AppUtility.isNotEmpty(orderbyValue)) {
			if (orderbyValue.toLowerCase().equals("desc")) {
				order = false;
			}
		}
		
		if (!AppUtility.isNotEmpty(orderbyColumn)) {
			queryBuilder = queryBuilder.orderBy(orderbyColumn, order);
		}
		
		try {
			//queryBuilderTotal = queryBuilder;
			queryBuilder = queryBuilder.limit(page.getPagesize()).offset(page.getPagecurr());
			
			if (AppUtility.isNotEmpty(columnNames)) {
				String[] columns = columnNames.split(",");
				if (columns.length > 0 && columns.length == columnValues.length) {
					Where where = queryBuilder.where();
					Where whereTotal = queryBuilderTotal.where();
					for (int i = 0; i < columns.length; i++) {
						if (AppUtility.isNotEmpty(columns[i])) {
							where.eq(columns[i], columnValues[i]);
							whereTotal.eq(columns[i], columnValues[i]);
						}
					}
					long count = whereTotal.countOf();
					page.setTotalcount((int)count);
					page.setResultlist(where.query());
					return page;
				}
			}
			long count = queryBuilderTotal.countOf();
			page.setTotalcount((int)count);
			page.setResultlist(queryBuilder.query());
			
			return page;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public Page queryPageStartEnd(Page page,Dao dao,String columnNames,Object[] columnValues,
			String orderbyColumn,String orderbyValue){
		boolean order = true;
		int pagecurr = page.getPagecurr();
		long count = 0;
		QueryBuilder queryBuilder = dao.queryBuilder();
		QueryBuilder queryBuilderTotal = dao.queryBuilder();
		try {
			if (AppUtility.isNotEmpty(columnNames)) {
				String[] columns = columnNames.split(",");
				if (columns.length > 0 && columns.length == columnValues.length) {
					Where whereTotal = queryBuilderTotal.where();
					for (int i = 0; i < columns.length; i++) {
						if (AppUtility.isNotEmpty(columns[i])) {
							whereTotal.eq(columns[i], columnValues[i]);
						}
					}
					count = whereTotal.countOf();
				}else{
					count = queryBuilderTotal.countOf();
				}
			}else{
				count = queryBuilderTotal.countOf();
			}
			int curr = (int)count-(page.getPagecurr()*page.getPagesize());
			if (curr < 0) {
				page.setPagecurr(1);
				page.setPagesize(0-curr);
			}else{
				page.setPagecurr(curr);
			}
			
			
			if (AppUtility.isNotEmpty(orderbyValue)) {
				if (orderbyValue.toLowerCase().equals("desc")) {
					order = false;
				}
			}
			
			if (!AppUtility.isNotEmpty(orderbyColumn)) {
				queryBuilder = queryBuilder.orderBy(orderbyColumn, order);
			}
		
		
			queryBuilder = queryBuilder.limit(page.getPagesize()).offset(page.getPagecurr());
			
			if (AppUtility.isNotEmpty(columnNames)) {
				String[] columns = columnNames.split(",");
				if (columns.length > 0 && columns.length == columnValues.length) {
					Where where = queryBuilder.where();
					for (int i = 0; i < columns.length; i++) {
						if (AppUtility.isNotEmpty(columns[i])) {
							where.eq(columns[i], columnValues[i]);
						}
					}
					page.setResultlist(where.query());
					page.setPagecurr(pagecurr);
					return page;
				}
			}
			page.setResultlist(queryBuilder.query());
			page.setPagecurr(pagecurr);
			return page;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
