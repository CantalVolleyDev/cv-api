package com.jtouzy.cv.api;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import com.jtouzy.cv.model.classes.Season;
import com.jtouzy.cv.model.dao.SeasonDAO;
import com.jtouzy.dao.DAOManager;

public class Test {
	public static void main(String[] args) {
		try {
			/*DAOManager.get();
			HashMap<String,Object> values = new HashMap<>();
			values.put("numsai", 0);
			values.put("prdsai", null);
			SeasonDAO dao = new SeasonDAO(Season.class);
			Season sai = dao.createFromValues(values);
			System.out.println(sai);
			System.out.println(dao.queryAll());
			Season s1 = dao.queryUnique(values);
			System.out.println(s1);
			Season s2 = dao.queryForOne(0);
			System.out.println(s2);*/
			//dao.update(s1);
			//dao.delete(sai);
			
			Connection connection = DriverManager.getConnection("jdbc:postgresql://5.135.146.110:5432/jto_cvapi_dvt", "upublic", "jtogri%010811sqlpublic");
			DAOManager.init("com.jtouzy.cv.model.classes");
			SeasonDAO dao = DAOManager.get().getDAO(connection, SeasonDAO.class);
			List<Season> lst = dao.queryAll();
			System.out.println(lst);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
