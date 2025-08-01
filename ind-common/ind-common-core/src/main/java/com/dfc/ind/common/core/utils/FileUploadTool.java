package com.dfc.ind.common.core.utils;

/**
 * 视频上传工具类
 *
 * @author ${Carlos}
 * on 2019/2/15
 */

import com.dfc.ind.common.core.utils.file.FileEntity;
import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.ServerInfo;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerGroup;
import org.csource.fastdfs.TrackerServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Iterator;
public class FileUploadTool  {

    private static final Logger log = LoggerFactory.getLogger(FileUploadTool.class);
    private static StorageClient storageClient = null;
    TransfMediaTool transfMediaTool = new TransfMediaTool();
    private static long upload_maxsize;
    private static String[] allowFiles;
    private static String[] allowImages;
    private static String[] allowFLV;
    private static String[] allowAVI;
    public FileUploadTool() {

    }


    private void initFastDFS() throws IOException {
        TrackerClient trackerClient = new TrackerClient(ClientGlobal.g_tracker_group);
        TrackerServer trackerServer = null;
        StorageServer storageServer = null;
        trackerServer = trackerClient.getConnection();
        if (trackerServer == null) {
        }else{
            trackerServer.close();
        }


        storageServer = trackerClient.getStoreStorage(trackerServer);
        if (storageServer == null) {
        }

        storageClient = new StorageClient1(trackerServer, storageServer);
    }

    public FileEntity createFile(MultipartFile multipartFile) {
        FileEntity entity = new FileEntity();
        boolean bflag = false;
        String fileName = multipartFile.getOriginalFilename().toString();
        entity.setCode(500);
        if (multipartFile.getSize() != 0L && !multipartFile.isEmpty()) {
            bflag = true;
            if (multipartFile.getSize() <= upload_maxsize) {
                if (this.checkFileType(fileName)) {
                    bflag = true;
                } else {
                    bflag = false;
                    entity.setMsg("文件类型不允许");
                }
            } else {
                bflag = false;
                entity.setMsg("文件大小超范围");
            }
        } else {
            bflag = false;
            entity.setMsg("文件为空");
        }

        if (bflag) {
            String fileNo = null;
            String newFileName = null;
            String url = "";
            TrackerClient trackerClient = new TrackerClient(ClientGlobal.g_tracker_group);
            TrackerServer trackerServer = null;
            StorageServer storageServer = null;

            String fileEnd;
            String size;
            try {
                trackerServer = trackerClient.getConnection();
                if (trackerServer == null) {
                    log.error("getConnection return null");
                }

                storageServer = trackerClient.getStoreStorage(trackerServer);
                if (storageServer == null) {
                    log.error("getStoreStorage return null");
                }

                storageClient = new StorageClient1(trackerServer, storageServer);
                String[] fileid = storageClient.upload_file(multipartFile.getBytes(), getFileExt(fileName), (NameValuePair[])null);
                if (fileid == null || fileid.length <= 0) {
                    entity.setMsg("获取服务器失败,请联系管理员...");
                    return entity;
                }

                fileEnd = fileid[0];
                size = fileid[1];
                ServerInfo[] serverInfos = trackerClient.getFetchStorages(trackerServer, fileEnd, size);
                url = "http://" + serverInfos[0].getIpAddr() + "/" + fileEnd + "/" + size;
                newFileName = this.getNewFileName(size);
            } catch (Exception var15) {
                throw new RuntimeException(var15);
            }finally {
                try {
                    trackerServer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            String origName = fileName.substring(0, fileName.lastIndexOf("."));
            fileEnd = getFileExt(fileName);
            if (!fileEnd.equals("mp4") && !fileEnd.equals("flv")) {
                entity.setType("01");
            } else {
                entity.setType("02");
            }

            size = this.getSize1(multipartFile);
            if (url != null && !"".equals(url)) {
                System.out.println("url不为空" + url);
                entity.setSize(size);
                entity.setPath(url);
                entity.setTitleOrig(origName);
                entity.setTitleAlter(newFileName);
                entity.setCode(200);
                entity.setMsg("上传成功");
            } else {
                entity.setMsg("保存路径为空,请联系管理员...");
            }
        }

        return entity;
    }

    public String uploadFile(byte[] content, String fileName) {
        TrackerServer trackerServer=null;
        try {
            TrackerClient trackerClient = new TrackerClient(ClientGlobal.g_tracker_group);
            trackerServer = trackerClient.getConnection();
            if (trackerServer == null) {
                log.error("getConnection return null");
            }

            StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
            if (storageServer == null) {
                log.error("getStoreStorage return null");
            }

            storageClient = new StorageClient1(trackerServer, storageServer);
            String[] fileid = storageClient.upload_appender_file( fileName, content, getFileExt(fileName), null);
//            String[] fileid = storageClient.upload_file(content, getFileExt(fileName), (NameValuePair[])null);
            String url = "";
            if (fileid != null && fileid.length > 0) {
                String groudName = fileid[0];
                String name = fileid[1];
                InetSocketAddress inetSocketAddress = trackerServer.getInetSocketAddress();
                InetAddress address = inetSocketAddress.getAddress();
                url = "http:/" + address + ":" + ClientGlobal.getG_tracker_http_port() + "/" + groudName + "/" + name;
                return url;
            } else {
                return null;
            }
        } catch (Exception var11) {
            return null;
        } finally {
            try {
                trackerServer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String upload(byte[] content, String fileName) {
        TrackerServer trackerServer = null;
        try {
            TrackerClient trackerClient = new TrackerClient(ClientGlobal.g_tracker_group);
            trackerServer = trackerClient.getConnection();
            if (trackerServer == null) {
                log.error("getConnection return null");
            }

            StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
            if (storageServer == null) {
                log.error("getStoreStorage return null");
            }

            storageClient = new StorageClient1(trackerServer, storageServer);
            String[] fileId = storageClient.upload_file(content, getFileExt(fileName), (NameValuePair[])null);
            if (fileId != null && fileId.length > 0) {
                String groupName = fileId[0];
                String name = fileId[1];
                String fileidStr = groupName + "/" + name;
                return fileidStr;
            } else {
                return null;
            }
        } catch (Exception var11) {
            return null;
        } finally {
            try {
                trackerServer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getNewFileName(String name) {
        String[] strings = name.split("/");
        String[] strings1 = strings[3].split("\\.");
        return strings1[0];
    }

    private boolean checkFileType(String fileName) {
        Iterator type = Arrays.asList(allowFiles).iterator();

        String ext;
        do {
            if (!type.hasNext()) {
                return false;
            }

            ext = (String)type.next();
        } while(!fileName.toLowerCase().endsWith(ext));

        return true;
    }

    private static String getFileExt(String fileName) {
        return !StringUtils.isBlank(fileName) && fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".") + 1) : "";
    }

    private String getName(String fileName) {
        Iterator type = Arrays.asList(allowFiles).iterator();

        String ext;
        do {
            if (!type.hasNext()) {
                return "";
            }

            ext = (String)type.next();
        } while(!fileName.contains(ext));

        String newFileName = fileName.substring(0, fileName.lastIndexOf(ext));
        return newFileName;
    }

    private String getSize1(MultipartFile multipartFile) {
        String size = "";
        long fileLength = multipartFile.getSize();
        DecimalFormat df = new DecimalFormat("#.00");
        if (fileLength < 1024L) {
            size = df.format((double)fileLength) + "BT";
        } else if (fileLength < 1048576L) {
            size = df.format((double)fileLength / 1024.0D) + "KB";
        } else if (fileLength < 1073741824L) {
            size = df.format((double)fileLength / 1048576.0D) + "MB";
        } else {
            size = df.format((double)fileLength / 1.073741824E9D) + "GB";
        }

        return size;
    }

    public int deleteFile(String fileId) {
        int result = 0;

        try {
            this.initFastDFS();
            result = ((StorageClient1)storageClient).delete_file1(fileId);
        } catch (IOException var4) {
            var4.printStackTrace();
        } catch (MyException var5) {
            var5.printStackTrace();
        }

        return result;
    }

    public InputStream downloadFile(String fileId) {
        try {
            this.initFastDFS();
            byte[] bytes = ((StorageClient1)storageClient).download_file1(fileId);
            InputStream inputStream = new ByteArrayInputStream(bytes);
            return inputStream;
        } catch (Exception var4) {
            return null;
        }
    }


    static{

        try {
            IniFileStream iniFileStream = new IniFileStream("fdfs_client.conf");
            int connect_timeout = iniFileStream.getIntValue("connect_timeout", 5);
            if (connect_timeout < 0) {
                connect_timeout = 5;
            }

            connect_timeout *= 1000;
            int network_timeout = iniFileStream.getIntValue("network_timeout", 30);
            if (network_timeout < 0) {
                network_timeout = 30;
            }

            network_timeout *= 1000;
            String charset = iniFileStream.getStrValue("charset");
            if (charset == null || charset.length() == 0) {
                charset = "ISO8859-1";
            }

            String[] tracker_servers = iniFileStream.getValues("tracker_server");
            if (com.dfc.ind.common.core.utils.StringUtils.isNotEmpty(System.getenv("tracker_server"))){
                //从系统环境变量中获取
                tracker_servers=System.getenv("tracker_server").split(",");
            }
            if (tracker_servers == null) {
                throw new MyException("item \"tracker_server  not found");
            }

            int http_port = iniFileStream.getIntValue("http.tracker_http_port", 81);
            if (com.dfc.ind.common.core.utils.StringUtils.isNotEmpty(System.getenv("tracker_http_port"))){
                http_port=Integer.parseInt(System.getenv("tracker_http_port")) ;
            }

            boolean anti_steal_token = iniFileStream.getBoolValue("http.anti_steal_token", false);
            String secret_key = null;
            if (anti_steal_token) {
                secret_key = iniFileStream.getStrValue("http.secret_key");
            }

            InetSocketAddress[] inetSocketAddresses = new InetSocketAddress[tracker_servers.length];

            for(int i = 0; i < tracker_servers.length; ++i) {
                String[] parts = tracker_servers[i].split("\\:", 2);
                if (parts.length != 2) {
                    throw new MyException("the value of item \"tracker_server\" is invalid, the correct format is host:port");
                }

                inetSocketAddresses[i] = new InetSocketAddress(parts[0].trim(), Integer.parseInt(parts[1].trim()));
            }

            ClientGlobal.setG_connect_timeout(connect_timeout);
            ClientGlobal.setG_network_timeout(network_timeout);
            ClientGlobal.setG_charset(charset);
            ClientGlobal.setG_tracker_http_port(http_port);
            ClientGlobal.setG_anti_steal_token(anti_steal_token);
            ClientGlobal.setG_secret_key(secret_key);
            ClientGlobal.setG_tracker_group(new TrackerGroup(inetSocketAddresses));
        } catch (Exception var11) {
            throw new RuntimeException(var11);
        }

        upload_maxsize = 524288000L;
        allowFiles = new String[]{".rar", ".doc", ".docx", ".zip", ".pdf", ".txt", ".swf", ".xlsx", ".gif", ".png", ".jpg", ".jpeg", ".bmp", ".xls", ".mp4", ".flv", ".ppt", ".avi", ".mpg", ".wmv", ".3gp", ".mov", ".asf", ".asx", ".vob", ".wmv9", ".rm", ".rmvb"};
        allowImages = new String[]{".jpg", ".png", ".pdf", ".jpeg", ".gif", ".JPG", ".PNG", ".PDF"};
        allowFLV = new String[]{".avi", ".mpg", ".wmv", ".3gp", ".mov", ".asf", ".asx", ".vob"};
        allowAVI = new String[]{".wmv9", ".rm", ".rmvb"};
    }


}
