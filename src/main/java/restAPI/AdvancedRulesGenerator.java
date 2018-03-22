package restAPI;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
public class AdvancedRulesGenerator {
	public static JsonElement getJson(String json, String key)
	{
		JsonParser parser = new JsonParser();
    	JsonObject jo = parser.parse(json).getAsJsonObject();
    	return (JsonElement) (jo.get(key));
	}
	//this method ommits the "" in the strings
	public static String getValue(String json, String key) 
	{
						
		JsonParser parser = new JsonParser();
    	JsonObject jo = parser.parse(json).getAsJsonObject();
    	return (jo.get(key).toString().replaceAll("\"", ""));
	}
	public static String ruleConstructor(String json) 
	{
		String rule="";
		JsonParser parser = new JsonParser();
		JsonObject jRead = parser.parse(json).getAsJsonObject();
		JsonObject jWrite= new JsonObject();
		JsonObject jActRead = (JsonObject) jRead.get("action");
		jWrite.addProperty("name", getValue(json, "name"));
		

		if(jActRead.get("name").toString().equalsIgnoreCase("\"email\""))
		{
			
			jWrite.addProperty("text", queryConstructor(json));
			JsonObject jaction = new JsonObject();
			JsonObject jprmt = new JsonObject();
			jprmt.add("to", jActRead.get("adressTo"));
			// fiwoo.platform@gmail.com
			jprmt.addProperty("from", "kibanaalert@gmail.com");
			jprmt.addProperty("subject", getValue(jActRead.toString(),"subject").toString());
			jaction.addProperty("type", "email");
			
			jaction.addProperty("template", getValue(jActRead.toString(),"template").toString());
			jaction.add("parameters", jprmt);
			jWrite.add("action", jaction);
			rule=jWrite.toString();
		}
		//if it isn't an email is an update
		else
		{
			
			JsonObject jus= (JsonObject) jActRead.get("updatedSensor");
			jWrite.addProperty("text", queryConstructor(json));
			JsonObject jaction = new JsonObject();
			JsonObject jprmt = new JsonObject();
			jprmt.addProperty("id", getValue(jus.toString(), "id"));
			//jprmt.addProperty("type", getValue(jus.toString(), "type"));
			JsonArray attributes= new JsonArray();
			for(JsonElement je : (JsonArray)jus.get("attributes"))
			{
				JsonObject jor = (JsonObject)je;
				attributes.add(jor);			
				
			}
			jprmt.add("attributes", attributes);
			jaction.addProperty("type", "update");
			jaction.add("parameters", jprmt);
			jWrite.add("action", jaction);
			rule=jWrite.toString();
		}
		JsonObject jo1 = new JsonObject();
		jo1.add("rule", jWrite);
		
		return jo1.toString();
	}
	public static String queryConstructor(String jsonRead)
	{
		String query="select*, \""+getValue(jsonRead, "name")+"\" as ruleName *,";
		String text=(String)getJson(jsonRead, "text").toString();
		JsonArray attributes = (JsonArray)getJson(text,"attributes");
		for(JsonElement je : attributes)
		{
			JsonObject jo = (JsonObject)je;
			query.concat(" ev."+getValue(jo.toString(), "name")+"? as"+getValue(jo.toString(), "name")+", ");
		}
		query=query.substring(0, query.length()-2)+" "
				+"from pattern [every ev=iotEvent(";
		for(JsonElement je : attributes)
		{
			JsonObject jo = (JsonObject)je;
			if(jo.get("type").toString().equalsIgnoreCase("\"String\""))
			{
				query=query+"cast("+getValue(jo.toString(), "name")+"?, String)"+getValue(jo.toString(),"operator")
				+getValue(jo.toString(),"value")+" and ";
			}
			else
			{
				query=query+"cast(cast("+getValue(jo.toString(), "name")+"?, String),"+getValue(jo.toString(), "type")+")"+getValue(jo.toString(),"operator")
				+getValue(jo.toString(),"value")+" and ";
			}
		}
		query=query.substring(0, query.length()-4)+"and type=\""+getValue(text, "sensorType")+"\" and cast(id?, String)"
				+ "=\""+getValue(text, "sensorId")+"\")]";
		return query;
	}
}