package com.teamchat.integration.giphy.classes;
/*
 * *
 * @author:Anuj Arora
 */
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class FixedHeightDownsampled {

@Expose
private String url;
@Expose
private String width;
@Expose
private String height;
@Expose
private String size;
@Expose
private String webp;
@SerializedName("webp_size")
@Expose
private String webpSize;

/**
* 
* @return
* The url
*/
public String getUrl() {
return url;
}

/**
* 
* @param url
* The url
*/
public void setUrl(String url) {
this.url = url;
}

/**
* 
* @return
* The width
*/
public String getWidth() {
return width;
}

/**
* 
* @param width
* The width
*/
public void setWidth(String width) {
this.width = width;
}

/**
* 
* @return
* The height
*/
public String getHeight() {
return height;
}

/**
* 
* @param height
* The height
*/
public void setHeight(String height) {
this.height = height;
}

/**
* 
* @return
* The size
*/
public String getSize() {
return size;
}

/**
* 
* @param size
* The size
*/
public void setSize(String size) {
this.size = size;
}

/**
* 
* @return
* The webp
*/
public String getWebp() {
return webp;
}

/**
* 
* @param webp
* The webp
*/
public void setWebp(String webp) {
this.webp = webp;
}

/**
* 
* @return
* The webpSize
*/
public String getWebpSize() {
return webpSize;
}

/**
* 
* @param webpSize
* The webp_size
*/
public void setWebpSize(String webpSize) {
this.webpSize = webpSize;
}

}