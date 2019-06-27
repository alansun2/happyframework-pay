package com.ehu.bean;

import com.ehu.weixin.entity.Mch;
import lombok.Getter;
import lombok.Setter;

/**
 * @author AlanSun
 * @date 2019/6/27 18:38
 **/
@Getter
@Setter
public class DownloadParam extends Mch {
    /**
     * 下载那一天的账单
     */
    private String time;
    /**
     * 下载文件存放绝对路径 （包含文件名）
     */
    private String desPath;
}
