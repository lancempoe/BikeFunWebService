package com.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * - Client will post an image name
 * - Service will send back a link to upload the image to the server
 * - Client will then post the image back.
 * - The client can then simply pull directly from a url for future use.
 * @author lancepoehler
 *
 */
@XmlRootElement
public class Image {

	public String name;
	public byte[] image;
	
}
