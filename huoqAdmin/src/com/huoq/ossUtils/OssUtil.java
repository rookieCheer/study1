package com.huoq.ossUtils;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import org.jfree.util.Log;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.internal.OSSUtils;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.PutObjectResult;
import com.huoq.common.util.QwyUtil;

/**
 * Description:oss上传文件使用
 * @author  changhaipeng
 * @date 上午10:11:24
 */
public class OssUtil {
	//工作空间名称
	public String bucketName;
	//ossClient
	public OSSClient ossClient;
	//账户信息
	private String endpoint;
	private String accessKeyId;
	private String secretAccessKey;
	//读取文件
	ResourceBundle resb;
	//支持的文件后缀
	private String suffix;
	
	
	public OssUtil(){
		this(null);
	}
	/***
	 * 通过直接加载oss.xml的方式读取
	 * 给出配置文件名称，加载配置文件名称，没有就默认oss.properties
	 * @param isLoadXml
	 */
	public OssUtil(String ossFileName){
		if(ossFileName==null||"".equals(ossFileName)){
			ossFileName = "app";
		}
		//读取文件
		if(resb==null){
			resb = ResourceBundle.getBundle(ossFileName);
		}
		
		this.bucketName = resb.getString("aliyun.oss.bucketName");
		this.accessKeyId = resb.getString("aliyun.oss.accessKeyId");
		this.secretAccessKey = resb.getString("aliyun.oss.secretAccessKey");
		this.endpoint = resb.getString("aliyun.oss.endpoint");
		this.suffix = resb.getString("aliyun.oss.upload.fileType");
		
		if(this.ossClient==null){
			this.ossClient = new OSSClient("http://"+endpoint, accessKeyId, secretAccessKey);
		}
	}

	/**
	 * Description:上传文件,传递相同的文件和路径会覆盖
	 * file:需要上传的文件
	 * filePath : 上传的oss文件路径，包含文件名和后缀（fullpath） ,自动生成相应的文件夹
	 * inputStream: 用文件流的方式，在file为null，inputStream不为null时使用
	 * return oss文件路径
	 * 使用最后要destroy();
	 * @author  changhaipeng
	 * @date 下午2:00:01
	 */
	public boolean uploadFile(File file,String filePath,InputStream inputStream){
		try {
			//文件流的方式
			if(file==null&&inputStream!=null){
				if(checkSuffix(filePath)){
					PutObjectResult os = ossClient.putObject(bucketName, filePath, inputStream);
					if(!QwyUtil.isNullAndEmpty(os)&&!QwyUtil.isNullAndEmpty(os.getETag())){
						return true;
					}else{
						Log.error("上传文件失败！fileName="+filePath+";");
						throw new OSSException();
					}
				}else{
					Log.info("上传后缀不符合要求！"+filePath);
					throw new OSSException();
				}
			}else{
				//文件的方式
				if(checkSuffix(filePath)){
					PutObjectResult os=ossClient.putObject(bucketName, filePath, file);
					
					if(!QwyUtil.isNullAndEmpty(os)&&!QwyUtil.isNullAndEmpty(os.getETag())){
						return true;
					}else{
						Log.error("上传文件失败！fileName="+filePath+";");
						throw new OSSException();
					}
					
				}else{
					Log.info("上传后缀不符合要求！"+filePath);
					throw new OSSException();
				}
			}
		} catch (OSSException e) {
			Log.error("上传文件失败！fileName="+filePath+";"+e);
			throw new OSSException();
		} catch (ClientException e) {
			Log.error("上传文件失败！fileName="+filePath+";"+e);
			throw new OSSException();
		}
	}
	

	/**
	 * Description:验证是否支持的文件后缀,传入文件的路径
	 * @author  changhaipeng
	 * @date 2017年6月22日 上午10:03:23
	 */
	private boolean checkSuffix(String filePath){
		if(QwyUtil.isNullAndEmpty(filePath)){
			Log.info("验证后缀传入参数为空！"+ filePath);
			return false;
		}
		
		String tempSuffix = filePath.substring(filePath.lastIndexOf(".")+1);
		if(suffix.contains(tempSuffix.toLowerCase())){
			return true;
		}
		return false;
	}
	
	public void destroy(){
		ossClient.shutdown();
	}

	public static void main(String[] args) {
		OssUtil ossUtil = new OssUtil();
		ossUtil.uploadFile(new File("F:\\QQ图片20170815160559.png"), "11.png", null);
	}
	
}
