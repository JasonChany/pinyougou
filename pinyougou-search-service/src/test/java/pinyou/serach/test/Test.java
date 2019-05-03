package pinyou.serach.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.pinyougou.search.service.ItemSearchService;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations="classpath*:spring/applicationContext*.xml")
public class Test {
	//@Autowired
	private ItemSearchService itemSearchService;
	
	//@org.junit.Test
	public void testSearch() {
		/*
		Map map=new HashMap();
		map.put("keywords", "华为");
		Map resultMap=itemSearchService.search(map);
		System.out.println(resultMap);
		*/
	}
}
