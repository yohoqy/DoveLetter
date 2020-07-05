package com.example.doveletter.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.doveletter.app.MyApplication;
import com.example.doveletter.bean.ReceiveInfo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;

import static javax.mail.internet.MimeUtility.decodeText;

public class ReceiveUtil {
   private String userAddress=MyApplication.userInfo.getUseraddress();
   private String pop3Server="pop."+userAddress.substring(userAddress.lastIndexOf("@")+1);
   private String protocol="pop3";
   private String password=MyApplication.userInfo.getUserpassword();
   private ArrayList<ReceiveInfo> receiveList=new ArrayList<>();

   public List<ReceiveInfo>receiveMail () throws Exception{

       Properties props = new Properties();
       props.setProperty("mail.transport.protocol", protocol); // 使用的协议（JavaMail规范要求）
       props.setProperty("mail.smtp.host", pop3Server); // 发件人的邮箱的 SMTP服务器地址

       // 获取连接
       Session session = Session.getInstance(props);
       session.setDebug(false);

       // 获取Store对象
       Store store = session.getStore(protocol);
       store.connect(pop3Server, userAddress, password); // POP3服务器的登陆认证

       // 通过POP3协议获得Store对象调用这个方法时，邮件夹名称只能指定为"INBOX"
       Folder folder = store.getFolder("INBOX");// 获得用户的邮件帐户
       folder.open(Folder.READ_WRITE); // 设置对邮件帐户的访问权限

       Message[] messages = folder.getMessages();// 得到邮箱帐户中的所有邮件

       for (Message message : messages){
           Address from = message.getFrom()[0];// 获得发送者地址
           String[] froms = from.toString().split("<");
           String senderAddress;
           if (froms.length != 1) {
               senderAddress = froms[1].substring(0, froms[1].length() - 1);
           } else {
               senderAddress = froms[0];
           }
           String subject=message.getSubject();// 获得邮件主题
           //获得邮件内容
           StringBuffer content = new StringBuffer(30);
           getMailTextContent(message, content);
           //邮件日期
//           SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm");
//           String date=formatter.format(message.getSentDate());
           Date date=message.getSentDate();
           //是否为新邮件
           boolean flag1;
           if(isNew(message)){
               flag1=true;
           }else{
               flag1=false;
           }

           ReceiveInfo receiveInfo=new ReceiveInfo(senderAddress,subject,content.toString(),date,flag1);

           //附件
           if(isContainAttachment(message)) {

               // TODO 创建文件有问题
//               File attachmentFile = new File(DateUtils.dateToDateString(date) + DateUtils.dateToTimeString(date) + ".jpg");

               String filePath = FileUtil.getFilePath() + DateUtils.dateToDateString(date) + DateUtils.dateToTimeString(date) + ".jpg";
               FileUtil.createFileByDeleteOldFile(filePath);
               File attachmentFile = new File(filePath);

               saveAttachment(message, attachmentFile);
               if (attachmentFile.length()>0){
                   receiveInfo.setFile(attachmentFile);
               }
           }

           receiveList.add(receiveInfo);
       }

       folder.close(false);// 关闭邮件夹对象
       store.close(); // 关闭连接对象


       //按日期排序
       Collections.sort(receiveList, new Comparator<ReceiveInfo>() {
           @Override
           public int compare(ReceiveInfo o1, ReceiveInfo o2) {
               return o2.getDate().compareTo(o1.getDate());
           }
       });

       return receiveList;
   }




    /**
     * 【判断此邮件是否已读，如果未读返回返回false,反之返回true】
     */
    private static boolean isNew(Message mimeMessage) throws MessagingException {
        boolean isnew = false;
        Flags flags = (mimeMessage).getFlags();
        Flags.Flag[] flag = flags.getSystemFlags();

        for (int i = 0; i < flag.length; i++) {
            if (flag[i] == Flags.Flag.SEEN) {
                isnew = true;
                break;
            }
        }
        return !isnew;
    }


    /**
     * 获得邮件文本内容
     *
     * @param part    邮件体
     * @param content 存储邮件文本内容的字符串
     */
    private void getMailTextContent(Part part, StringBuffer content) throws MessagingException, IOException {
        //如果是文本类型的附件，通过getContent方法可以取到文本内容，但这不是我们需要的结果，所以在这里要做判断
        boolean isContainTextAttach = part.getContentType().indexOf("name") > 0;
        if (part.isMimeType("text/plain") && !isContainTextAttach) {
            Log.d("chenchen", "111");
            content.append(part.getContent().toString());
        } else if (part.isMimeType("text/html") && !isContainTextAttach) {
            Log.d("chenchen", "222");
            //content.append(part.getContent().toString());
        } else if (part.isMimeType("message/rfc822")) {
            Log.d("chenchen", "333");
            getMailTextContent((Part) part.getContent(), content);
        } else if (part.isMimeType("multipart/*")) {
            Log.d("chenchen", "444");
            Multipart multipart = (Multipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                getMailTextContent(bodyPart, content);
            }
        }
    }


    /**
     * 判断邮件中是否包含附件
     * @return 邮件中存在附件返回true，不存在返回false
     * @throws MessagingException
     * @throws IOException
     */
    public static boolean isContainAttachment(Part part) throws MessagingException, IOException {
        boolean flag = false;
        if (part.isMimeType("multipart/*")) {
            MimeMultipart multipart = (MimeMultipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                String disp = bodyPart.getDisposition();
                if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE))) {
                    flag = true;
                } else if (bodyPart.isMimeType("multipart/*")) {
                    flag = isContainAttachment(bodyPart);
                } else {
                    String contentType = bodyPart.getContentType();
                    if (contentType.indexOf("application") != -1) {
                        flag = true;
                    }

                    if (contentType.indexOf("name") != -1) {
                        flag = true;
                    }
                }

                if (flag) break;
            }
        } else if (part.isMimeType("message/rfc822")) {
            flag = isContainAttachment((Part)part.getContent());
        }
        return flag;
    }



    /**
     * 保存附件
     *
     * @param part 邮件中多个组合体中的其中一个组合体
     * @param file 附件保存文件
     */
    public void saveAttachment(Part part, File file) throws UnsupportedEncodingException, MessagingException,
            FileNotFoundException, IOException {
        if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();    //复杂体邮件
            //复杂体邮件包含多个邮件体
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                //获得复杂体邮件中其中一个邮件体
                BodyPart bodyPart = multipart.getBodyPart(i);
                //某一个邮件体也有可能是由多个邮件体组成的复杂体
                String disp = bodyPart.getDisposition();
                if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE))) {
                    InputStream is = bodyPart.getInputStream();
                    saveFile(is, file);
                } else if (bodyPart.isMimeType("multipart/*")) {
                    saveAttachment(bodyPart, file);
                } else {
                    String contentType = bodyPart.getContentType();
                    if (contentType.indexOf("name") != -1 || contentType.indexOf("application") != -1) {
                        saveFile(bodyPart.getInputStream(), file);
                    }
                }
            }
        } else if (part.isMimeType("message/rfc822")) {
            saveAttachment((Part) part.getContent(), file);
        }
    }


    /**
     * 读取输入流中的数据保存至指定目录
     *
     * @param is   输入流
     * @param file 附件保存文件
     */
    private File saveFile(InputStream is, File file)
            throws FileNotFoundException, IOException {
        BufferedInputStream bis = new BufferedInputStream(is);
        BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(file));
        int len = -1;
        while ((len = bis.read()) != -1) {
            bos.write(len);
            bos.flush();
        }
        bos.close();
        bis.close();
        return file;
    }
}
