package com.jtouzy.cv.api.io;

public class FileInfos {
	public String fileUrl;
	public String filePath;
	public Long boxMaxHeight;
	public Long boxMaxWidth;
	public Long height;
	public Long imageHeight;
	public Long imageWidth;
	public Long width;
	public Long x;
	public Long y;
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FileInfos [fileUrl=");
		builder.append(fileUrl);
		builder.append(", filePath=");
		builder.append(filePath);
		builder.append(", boxMaxHeight=");
		builder.append(boxMaxHeight);
		builder.append(", boxMaxWidth=");
		builder.append(boxMaxWidth);
		builder.append(", height=");
		builder.append(height);
		builder.append(", imageHeight=");
		builder.append(imageHeight);
		builder.append(", imageWidth=");
		builder.append(imageWidth);
		builder.append(", width=");
		builder.append(width);
		builder.append(", x=");
		builder.append(x);
		builder.append(", y=");
		builder.append(y);
		builder.append("]");
		return builder.toString();
	}
}
