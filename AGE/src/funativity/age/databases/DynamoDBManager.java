package funativity.age.databases;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import android.os.AsyncTask;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.dynamodb.*;
import com.amazonaws.services.dynamodb.model.AttributeValue;
import com.amazonaws.services.dynamodb.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodb.model.CreateTableRequest;
import com.amazonaws.services.dynamodb.model.DeleteItemRequest;
import com.amazonaws.services.dynamodb.model.GetItemRequest;
import com.amazonaws.services.dynamodb.model.Key;
import com.amazonaws.services.dynamodb.model.KeySchema;
import com.amazonaws.services.dynamodb.model.KeySchemaElement;
import com.amazonaws.services.dynamodb.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodb.model.PutItemRequest;
import com.amazonaws.services.dynamodb.model.UpdateItemRequest;

/**
 * The data access class for Amazon's DynamoDB
 * 
 * @author binisha
 * 
 */
public class DynamoDBManager
{
	/**
	 * The reference to the DynamoDB to be used for connectivity
	 */
	private AmazonDynamoDB db;

	/**
	 * Constructor for DynamoDBManager that sets up the databases if needed and
	 * establishes the connection for use
	 * 
	 * @param relativePath
	 *            The relative path to the AWS properties file containing the
	 *            secretKey and accessKey
	 * @throws IOException
	 *             if the credentials fail
	 */
	public DynamoDBManager(String relativePath) throws IOException
	{
		db = new AmazonDynamoDBClient(new PropertiesCredentials(
				DynamoDBManager.class.getResourceAsStream(relativePath)));
		setupTables();
	}

	/**
	 * Constructor for DynamoDBManager that sets up the databases if needed and
	 * establishes the connection for use
	 * 
	 * @param propertiesFile
	 *            The file containing the AWS properties for secretKey and
	 *            accessKey
	 * @throws IOException
	 *             if credentials fail
	 */
	public DynamoDBManager(File propertiesFile) throws IOException
	{
		db = new AmazonDynamoDBClient(new PropertiesCredentials(propertiesFile));
		setupTables();
	}

	/**
	 * This will return the actual DynamoDB reference for developer usage
	 * 
	 * @return the AmazonDynamoDB reference
	 */
	public AmazonDynamoDB getAmazonDynamoDB()
	{
		return db;
	}

	/**
	 * Overwrite the user with the given map of attributes and property names.
	 * NOTE: One of the key value pairs must contain the primary key
	 * 
	 * @param dataToOverwrite
	 *            The map of attribute names and values to save for the user
	 */
	public void overwriteUser(Map<String, AttributeValue> dataToOverwrite)
	{
		PutItemRequest putRequest = new PutItemRequest();
		putRequest.setTableName("User");
		putRequest.setItem(dataToOverwrite);
		try
		{
			new PutItemAsync().execute(putRequest).get();
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ExecutionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Overwrite the data with the given map of attributes and property names.
	 * This will replace any existing data for this primary key with the data in
	 * the map NOTE: One of the key value pairs must contain the primary key
	 * 
	 * @param dataToOverwrite
	 *            The map of attribute names and values to save
	 */
	public void overwriteData(Map<String, AttributeValue> dataToOverwrite)
	{
		PutItemRequest putRequest = new PutItemRequest();
		putRequest.setTableName("Data");
		putRequest.setItem(dataToOverwrite);
		try
		{
			new PutItemAsync().execute(putRequest).get();
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ExecutionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Update the user with the given map of attributes and property names This
	 * will update any existing attributes with the specified name for that
	 * primary key but will not delete any other data for that primary key NOTE:
	 * One of the key value pairs must contain the primary key
	 * 
	 * @param dataToUpdate
	 *            The map of attribute names and values to save.
	 * @param primaryKey
	 *            The primary key of the object to update
	 */
	public void updateUser(Map<String, AttributeValue> dataToUpdate,
			String primaryKey)
	{
		UpdateItemRequest updateRequest = new UpdateItemRequest();
		updateRequest.setTableName("User");
		updateRequest.setKey(new Key().withHashKeyElement(new AttributeValue()
				.withS(primaryKey)));
		Map<String, AttributeValueUpdate> updates = new HashMap<String, AttributeValueUpdate>();

		// Convert all of the AttributeValeus to AttributeValueUpdates in order
		// to keep the interface
		// simple for the developer
		for (Map.Entry<String, AttributeValue> entry : dataToUpdate.entrySet())
		{
			updates.put(entry.getKey(),
					new AttributeValueUpdate().withValue(entry.getValue()));
		}
		updateRequest.setAttributeUpdates(updates);

		try
		{
			new UpdateItemAsync().execute(updateRequest).get();
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ExecutionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Update the data with the given map of attributes and property names This
	 * will update any existing attributes with the specified name for that
	 * primary key but will not delete any other data for that primary key NOTE:
	 * One of the key value pairs must contain the primary key
	 * 
	 * @param dataToUpdate
	 *            The map of attribute names and values to save.
	 * @param primaryKey
	 *            The primary key of the obejct to update
	 */
	public void updateData(Map<String, AttributeValue> dataToUpdate,
			String primaryKey)
	{
		UpdateItemRequest updateRequest = new UpdateItemRequest();
		updateRequest.setTableName("Data");
		updateRequest.setKey(new Key().withHashKeyElement(new AttributeValue()
				.withS(primaryKey)));

		Map<String, AttributeValueUpdate> updates = new HashMap<String, AttributeValueUpdate>();
		// Convert all of the AttributeValeus to AttributeValueUpdates in order
		// to keep the interface
		// simple for the developer
		for (Map.Entry<String, AttributeValue> entry : dataToUpdate.entrySet())
		{
			updates.put(entry.getKey(),
					new AttributeValueUpdate().withValue(entry.getValue()));
		}
		updateRequest.setAttributeUpdates(updates);

		try
		{
			new UpdateItemAsync().execute(updateRequest).get();
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ExecutionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Get the user with the given key. This will return all attributes for that
	 * user as a map of attribute name and attribute values
	 * 
	 * @param key
	 *            The primary key value of the user to get
	 * @return A map of all the attribute name value pairs for that user
	 */
	public Map<String, AttributeValue> getUserAsMap(String key)
	{
		GetItemRequest getRequest = new GetItemRequest();
		getRequest.setKey(new Key().withHashKeyElement(new AttributeValue()
				.withS(key)));
		getRequest.setTableName("User");
		try
		{
			return new GetItemAsync().execute(getRequest).get();
		}
		catch (InterruptedException e)
		{
			// If an exception occurs return null
		}
		catch (ExecutionException e)
		{
			// If an exception occurs return null
		}
		return null;
	}

	/**
	 * Get the data with the given key. This will return all attributes for that
	 * data as a map of attribute name and attribute values
	 * 
	 * @param key
	 *            The primary key value of the data item to get
	 * @return A map of all the attribute name value pairs for that data item
	 */
	public Map<String, AttributeValue> getDataAsMap(String key)
	{
		GetItemRequest getRequest = new GetItemRequest();
		getRequest.setKey(new Key().withHashKeyElement(new AttributeValue()
				.withS(key)));
		getRequest.setTableName("Data");
		try
		{
			return new GetItemAsync().execute(getRequest).get();
		}
		catch (InterruptedException e)
		{
			// If an exception occurs return null
		}
		catch (ExecutionException e)
		{
			// If an exception occurs return null
		}
		return null;
	}

	/**
	 * Get the specific value of an attribute name for a specified user by its
	 * primary key
	 * 
	 * @param key
	 *            The primary key of the user to get
	 * @param attributeName
	 *            The name of the attribute to get the value for
	 * @return An AttributeValue containing the value of the attribute
	 *         requested. Null if the attribute doesn't exist
	 */
	public AttributeValue getUser(String key, String attributeName)
	{
		AttributeValue returnValue = null;

		GetItemRequest getRequest = new GetItemRequest();
		getRequest.setKey(new Key().withHashKeyElement(new AttributeValue()
				.withS(key)));
		getRequest.setTableName("User");

		Map<String, AttributeValue> result = null;
		try
		{
			result = new GetItemAsync().execute(getRequest).get();
		}
		catch (InterruptedException e)
		{
			// If an exception occurs return null
		}
		catch (ExecutionException e)
		{
			// If an exception occurs return null
		}
		if (result != null)
		{
			returnValue = result.get(attributeName);
		}
		return returnValue;
	}

	/**
	 * Get the specific value of an attribute name for the specified data item
	 * by its primary key
	 * 
	 * @param key
	 *            The primary key of the data item to get
	 * @param attributeName
	 *            The name of the attribute to get the value for
	 * @return An AttributeValue containing the value of the attribute
	 *         requested. Null if the attribute doesn't exist
	 */
	public AttributeValue getData(String key, String keyToData)
	{
		AttributeValue returnValue = null;

		GetItemRequest getRequest = new GetItemRequest();
		getRequest.setKey(new Key().withHashKeyElement(new AttributeValue()
				.withS(key)));
		getRequest.setTableName("Data");

		Map<String, AttributeValue> result = null;
		try
		{
			result = new GetItemAsync().execute(getRequest).get();
		}
		catch (InterruptedException e)
		{
			// If an exception occurs return null
		}
		catch (ExecutionException e)
		{
			// If an exception occurs return null
		}
		if (result != null)
		{
			returnValue = result.get(keyToData);
		}
		return returnValue;
	}

	/**
	 * Delete the specific user from the table
	 * 
	 * @param primaryKey
	 *            The primary key of the user to delete
	 */
	public void deleteUser(String primaryKey)
	{
		DeleteItemRequest deleteRequest = new DeleteItemRequest();
		deleteRequest.setKey(new Key().withHashKeyElement(new AttributeValue()
				.withS(primaryKey)));
		try
		{
			new DeleteItemAsync().execute(deleteRequest).get();
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ExecutionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Delete the specific data from the table
	 * 
	 * @param primaryKey
	 *            The primary key of the data set to delete
	 */
	public void deleteData(String primaryKey)
	{
		DeleteItemRequest deleteRequest = new DeleteItemRequest();
		deleteRequest.setKey(new Key().withHashKeyElement(new AttributeValue()
				.withS(primaryKey)));
		try
		{
			new DeleteItemAsync().execute(deleteRequest).get();
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ExecutionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * This will create the tables if they do not already exist for User and
	 * Data
	 */
	private void setupTables()
	{
		List<String> tableNames = new ArrayList<String>();

		try
		{
			tableNames = new GetTablesAsync().execute().get();
			if (!tableNames.contains("User"))
			{
				new CreateTableAsync().execute("User").get();
			}
			if (!tableNames.contains("Data"))
			{
				new CreateTableAsync().execute("Data").get();
			}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		catch (ExecutionException e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * This class will asynchronously get the tables in the database
	 * 
	 * @author binisha
	 * 
	 */
	private class GetTablesAsync extends AsyncTask<Void, Void, List<String>>
	{

		@Override
		protected List<String> doInBackground(Void... arg0)
		{
			return db.listTables().getTableNames();
		}
	}

	/**
	 * This class will asynchronously create a table given a table name
	 * 
	 * @author binisha
	 * 
	 */
	private class CreateTableAsync extends AsyncTask<String, Void, Void>
	{

		@Override
		protected Void doInBackground(String... params)
		{
			db.createTable(new CreateTableRequest(params[0], new KeySchema(
					new KeySchemaElement().withAttributeName("primaryKey")
							.withAttributeType("S")))
					.withProvisionedThroughput(new ProvisionedThroughput()
							.withReadCapacityUnits(1L).withWriteCapacityUnits(
									1L)));
			return null;
		}

	}

	/**
	 * This class will asynchronously get the item requested and return it Will
	 * return null if an exception is encountered
	 * 
	 * @author binisha
	 * 
	 */
	private class GetItemAsync extends
			AsyncTask<GetItemRequest, Void, Map<String, AttributeValue>>
	{

		@Override
		protected Map<String, AttributeValue> doInBackground(
				GetItemRequest... params)
		{
			try
			{
				return db.getItem(params[0].withConsistentRead(true)).getItem();
			}
			catch (AmazonServiceException e)
			{
				// If an exeception is thrown return null
			}
			catch (AmazonClientException e)
			{
				// If an exeception is thrown return null
			}

			return null;
		}
	}

	/**
	 * This class will asynchronously put the item requested and overwrite any
	 * existing one
	 * 
	 * @author binisha
	 * 
	 */
	private class PutItemAsync extends AsyncTask<PutItemRequest, Void, Void>
	{

		@Override
		protected Void doInBackground(PutItemRequest... params)
		{
			db.putItem(params[0]);

			// Must return null as Void is a class that should signify there
			// is no returned data but something needs to be returned
			return null;
		}
	}

	/**
	 * This class will asynchronously update the item if it exists and create a
	 * new one if it does not
	 * 
	 * @author binisha
	 * 
	 */
	private class UpdateItemAsync extends
			AsyncTask<UpdateItemRequest, Void, Void>
	{

		@Override
		protected Void doInBackground(UpdateItemRequest... params)
		{
			db.updateItem(params[0]);
			// Must return null as Void is a class that should signify there
			// is no returned data but something needs to be returned
			return null;
		}
	}

	/**
	 * This class will asynchronously delete the item if it exists
	 * 
	 * @author binisha
	 * 
	 */
	private class DeleteItemAsync extends
			AsyncTask<DeleteItemRequest, Void, Void>
	{

		@Override
		protected Void doInBackground(DeleteItemRequest... params)
		{
			db.deleteItem(params[0]);
			return null;
		}
	}
}
