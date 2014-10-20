package com.billylindeman.dust.particle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.billylindeman.dust.util.Color;
import com.billylindeman.dust.util.Vector2;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by billy on 10/13/14.
 */
public class EmitterConfig {
    int emitterType;

    public Bitmap texture;

    Vector2 sourcePosition, sourcePositionVariance;
    float angle, angleVariance;
    float speed, speedVariance;
    float radialAcceleration, tangentialAcceleration;
    float radialAccelVariance, tangentialAccelVariance;
    Vector2 gravity;
    float particleLifespan, particleLifespanVariance;

    Color startColor, startColorVariance;
    Color finishColor, finishColorVariance;

    float startParticleSize, startParticleSizeVariance;
    float finishParticleSize, finishParticleSizeVariance;

    int maxParticles;


    float duration;
    float rotationStart, rotationStartVariance;
    float rotationEnd, rotationEndVariance;


    float maxRadius;
    float maxRadiusVariance;
    float radiusSpeed;
    float minRadius;
    float minRadiusVariance;
    float rotatePerSecond;
    float rotatePerSecondVariance;

    public static EmitterConfig fromStream(InputStream stream) {
        EmitterConfig config = new EmitterConfig();
        config.buildFromXMLStream(stream);
        return config;
    }

    public void buildFromXMLStream(InputStream stream) {
        try {

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(stream);
            extractConfigOptions(doc);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void extractConfigOptions(Document doc) throws Exception {
        emitterType                 = parseIntValueForElement(doc,   "emitterType");
        sourcePosition              = parsePointValueForElement(doc, "sourcePosition");
        sourcePositionVariance      = parsePointValueForElement(doc, "sourcePositionVariance");
        speed                       = parseFloatValueForElement(doc, "speed");
        speedVariance               = parseFloatValueForElement(doc, "speedVariance");
        particleLifespan            = parseFloatValueForElement(doc, "particleLifeSpan");
        particleLifespanVariance    = parseFloatValueForElement(doc, "particleLifespanVariance");
        angle                       = parseFloatValueForElement(doc, "angle");
        angleVariance               = parseFloatValueForElement(doc, "angleVariance");
        gravity                     = parsePointValueForElement(doc, "gravity");
        radialAcceleration          = parseFloatValueForElement(doc, "radialAcceleration");
        radialAccelVariance         = parseFloatValueForElement(doc, "radialAccelVariance");
        tangentialAcceleration      = parseFloatValueForElement(doc, "tangentialAcceleration");
        tangentialAccelVariance     = parseFloatValueForElement(doc, "tangentialAccelVariance");
        startColor                  = parseColorValueForElement(doc, "startColor");
        startColorVariance          = parseColorValueForElement(doc, "startColorVariance");
        finishColor                 = parseColorValueForElement(doc, "finishColor");
        finishColorVariance         = parseColorValueForElement(doc, "finishColorVariance");
        maxParticles                = parseIntValueForElement(doc,   "maxParticles");
        startParticleSize           = parseFloatValueForElement(doc, "startParticleSize");
        startParticleSizeVariance   = parseFloatValueForElement(doc, "startParticleSizeVariance");
        finishParticleSize          = parseFloatValueForElement(doc, "finishParticleSize");
        finishParticleSizeVariance  = parseFloatValueForElement(doc, "finishParticleSizeVariance");
        duration                    = parseFloatValueForElement(doc, "duration");

        //Used for particles spinning around source location
        maxRadius                   = parseFloatValueForElement(doc, "maxRadius");
        maxRadiusVariance           = parseFloatValueForElement(doc, "maxRadiusVariance");
        minRadius                   = parseFloatValueForElement(doc, "minRadius");
        minRadiusVariance           = parseFloatValueForElement(doc, "minRadiusVariance");
        rotatePerSecond             = parseFloatValueForElement(doc, "rotatePerSecond");
        rotatePerSecondVariance     = parseFloatValueForElement(doc, "rotatePerSecondVariance");
        rotationStart               = parseFloatValueForElement(doc, "rotationStart");
        rotationStartVariance       = parseFloatValueForElement(doc, "rotationStartVariance");
        rotationEnd                 = parseFloatValueForElement(doc, "rotationEnd");
        rotationEndVariance         = parseFloatValueForElement(doc, "rotationEndVariance");


        texture = parseBase64GzippedTextureFromDocument(doc);
        if(texture == null) {
            Log.d("EmitterConfig", "Texture format in PEX file unsupported!");
        }
    }


    private int parseIntValueForElement(Document doc, String name) {
        NodeList elements = doc.getElementsByTagName(name);
        Element element = (Element)elements.item(0);
        return Integer.parseInt(element.getAttribute("value"));
    }

    private float parseFloatValueForElement(Document doc, String name) {
        NodeList elements = doc.getElementsByTagName(name);
        Element element = (Element)elements.item(0);
        return Float.parseFloat(element.getAttribute("value"));
    }

    private boolean parseBooleanValueForElement(Document doc, String name) {
        NodeList elements = doc.getElementsByTagName(name);
        Element element = (Element)elements.item(0);
        return Boolean.parseBoolean(element.getAttribute("value"));
    }

    private Vector2 parsePointValueForElement(Document doc, String name) {
        NodeList elements = doc.getElementsByTagName(name);
        Element element = (Element)elements.item(0);
        float x = Float.parseFloat(element.getAttribute("x"));
        float y = Float.parseFloat(element.getAttribute("y"));
        return new Vector2(x,y);
    }

    /**
     * PEX files have textures that are Base64 encoded and GZIP Deflate'd
     * this function decodes and inflates them into a bitmap
     */
    private Bitmap parseBase64GzippedTextureFromDocument(Document doc) throws Exception {
        NodeList elements = doc.getElementsByTagName("texture");
        Element element = (Element)elements.item(0);
        String name = element.getAttribute("name");
        String base64Data = element.getAttribute("data");

        byte[] compressedImageBytes = Base64.decode(base64Data, Base64.DEFAULT);
        GZIPInputStream decrompressionStream = new GZIPInputStream(new ByteArrayInputStream(compressedImageBytes));

        return BitmapFactory.decodeStream(decrompressionStream);
    }

    private Color parseColorValueForElement(Document doc, String name) {
        NodeList elements = doc.getElementsByTagName(name);
        Element element = (Element)elements.item(0);

        float r = Float.parseFloat(element.getAttribute("red"));
        float g = Float.parseFloat(element.getAttribute("green"));
        float b = Float.parseFloat(element.getAttribute("blue"));
        float a = Float.parseFloat(element.getAttribute("alpha"));

        return new Color(r,g,b,a);
    }

}
