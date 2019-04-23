package com.pinyougou.shop.controller;
/**
 * 文件上传
 * @author Jason
 *
 */

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import entity.Result;
import utils.FastDFSClient;

@RestController
public class UploadController {
	
	@Value("${FILE_SERVER_URL}")
	private String FILE_SERVER_URL;
	
	@RequestMapping("/upload")
	public Result upload(MultipartFile file) {
		//获取文件名
		String filename = file.getOriginalFilename();
		//获取文件扩展名
		String extName = filename.substring(filename.lastIndexOf(".")+1);
		try {
			//创建FastDFS客户端
			FastDFSClient dfsClient = new FastDFSClient("classpath:config/fdfs_client.conf");
			//执行上传(返回服务器文件路径)
			String path= dfsClient.uploadFile(file.getBytes(), extName);
			//拼接服务器地址与文件服务器路径
			String url=FILE_SERVER_URL+path;
			System.out.println(url);
			//返回
			return new Result(true, url);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "上传失败");
		}
	}

}
