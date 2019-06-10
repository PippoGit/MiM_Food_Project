package it.unipi.ing.mim.img.elasticsearch;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;

import it.unipi.ing.mim.deep.ImgDescriptor;
import it.unipi.ing.mim.deep.Parameters;
import it.unipi.ing.mim.deep.tools.FeaturesStorage;

public class ElasticImgIndexing implements AutoCloseable {
	
	private Pivots pivots;
	
	private List<ImgDescriptor> imgDescDataset;
	private int topKIdx;
	
	private RestHighLevelClient client;
		
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		try (ElasticImgIndexing esImgIdx = new ElasticImgIndexing(Parameters.PIVOTS_FILE_GOOGLENET, Parameters.STORAGE_FILE, Parameters.TOP_K_IDX)) {
			esImgIdx.createIndex();
			esImgIdx.index();	
		}
		
	}
	
	//TODO
	public ElasticImgIndexing(File pivotsFile, File datasetFile, int topKIdx) throws IOException, ClassNotFoundException {
		//Initialize pivots, imgDescDataset, REST
		pivots = new Pivots(pivotsFile);
		imgDescDataset = FeaturesStorage.load(datasetFile);
		this.topKIdx = topKIdx;
		
		RestClientBuilder builder = RestClient.builder(new HttpHost("localhost",9200,"http"));
		client = new RestHighLevelClient(builder);
	}
	
	//TODO
	public void close() throws IOException {
		//close REST client
		client.close();
	}
	
	//TODO
	public void createIndex() throws IOException {
		//Create the Elasticsearch index
		IndicesClient idx = client.indices();
		CreateIndexRequest request = new CreateIndexRequest (Parameters.INDEX_NAME);
		Builder s = Settings.builder().put("index.number_of_shards",1).put("index.number_of_replicas",0).put("analysis.analyzer.first.type","whitespace");
	
		request.settings(s);
		idx.create(request,RequestOptions.DEFAULT);
		
		System.out.println("INDEX CREATED");
	}
	
	//TODO
	public void index() throws IOException {
		//LOOP
			//index all dataset features into Elasticsearch
		IndexRequest request;
		
		for(ImgDescriptor el: imgDescDataset) {
			request = composeRequest(el.getId(),pivots.features2Text(el, topKIdx), el.getFoodClass());
			client.index(request,RequestOptions.DEFAULT);
		}
	}
	
	//TODO
	private IndexRequest composeRequest(String id, String imgTxt, String foodClass){			
		//Initialize and fill IndexRequest Object with Fields.ID and Fields.IMG txt
		IndexRequest request = new IndexRequest(Parameters.INDEX_NAME,"doc");
		
		Map<String,Object> jsonMap = new HashMap<>();
		jsonMap.put(Fields.ID, id);
		jsonMap.put(Fields.IMG, imgTxt);
		jsonMap.put(Fields.FOOD_CLASS, foodClass);
		
		request.source(jsonMap);
		
		return request;
	}
}
