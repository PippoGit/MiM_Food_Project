package it.unipi.ing.mim.deep;

import java.io.Serializable;

public class ImgDescriptor implements Serializable, Comparable<ImgDescriptor> {

	private static final long serialVersionUID = 1L;
	
	private float[] normalizedVector; // image feature
	
	private String id; // unique id of the image (usually file name)
	
	private double dist; // used for sorting purposes
	
	private String foodClass; 
	
	public ImgDescriptor(float[] features, String id, String foodClass) {
		if (features != null) {
			float norm2 = evaluateNorm2(features);
			this.normalizedVector = getNormalizedVector(features, norm2);
		}
		this.id = id;
		this.foodClass = foodClass;
	}
	
	public float[] getFeatures() {
		return normalizedVector;
	}
	
    public String getId() {
		return id;
	}
    
    public String getFoodClass() {
		return foodClass;
	}

	public void setId(String id) {
		this.id = id;
	}

	public double getDist() {
		return dist;
	}

	public void setDist(double dist) {
		this.dist = dist;
	}

	// compare with other friends using distances
	@Override
	public int compareTo(ImgDescriptor arg0) {
		return Double.valueOf(dist).compareTo(arg0.dist);
	}
	
	//evaluate Euclidian distance
	public double distance(ImgDescriptor desc) {
		
		//System.out.println("Distance");
		float[] queryVector = desc.getFeatures();
		
		dist = 0;
		for (int i = 0; i < queryVector.length; i++) {
			dist += (normalizedVector[i] - queryVector[i]) * (normalizedVector[i] - queryVector[i]);
		}
		dist = Math.sqrt(dist);
		
		return dist;
	}
	
	//Normalize the vector values 
	private float[] getNormalizedVector(float[] vector, float norm) {
		if (norm != 0) {
			for (int i = 0; i < vector.length; i++) {
				vector[i] = vector[i]/norm;
			}
		}
		return vector;
	}
	
	//Norm 2
	private float evaluateNorm2(float[] vector) {
		float norm2 = 0;
		for (int i = 0; i < vector.length; i++) {
			norm2 += (vector[i]) * (vector[i]);
		}
		norm2 = (float) Math.sqrt(norm2);
		
		return norm2;
	}
    
}