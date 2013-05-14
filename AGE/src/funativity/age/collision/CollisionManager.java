package funativity.age.collision;

import java.util.ArrayList;
import java.util.List;

import android.util.SparseArray;

import funativity.age.state.Scene;

/**
 * 
 * This class is responsible for managing all collsions within the game.
 * Collisions are added to layers in order to help organize the collisions.
 * Only CollisionShapes on the same layer can collide.
 * 
 */
public class CollisionManager
{
	private SparseArray<List<CollisionShape>> layers = new SparseArray<List<CollisionShape>>();
	private SparseArray<List<CollisionShape>> addList = new SparseArray<List<CollisionShape>>();
	private SparseArray<List<CollisionShape>> removeList = new SparseArray<List<CollisionShape>>();
	private List<Integer> updateLayers = new ArrayList<Integer>();

	private final Scene scene;

	/**
	 * Create a CollisionManager using specified Scene.
	 * 
	 * @param scene
	 */
	public CollisionManager(Scene scene)
	{
		this.scene = scene;
	}

	/**
	 * Add the child to the specified layer
	 * 
	 * @param child
	 *            Child to be added to the collision manager
	 * @param layerNum
	 *            The layer the child should be added to
	 */
	public void addChild(CollisionShape child, int layerNum)
	{
		if (child == null)
			return;

		// Get the layer this child will be added to
		List<CollisionShape> add = addList.get(layerNum);

		// if this layer has not been made yet, make one
		if (add == null)
		{
			addList.put(layerNum, new ArrayList<CollisionShape>());
			add = addList.get(layerNum);
		}

		// Add child to list
		add.add(child);

		// add this layer to the updatelayer list, so this layer gets updated
		if (!updateLayers.contains(layerNum))
			updateLayers.add(layerNum);
	}

	/**
	 * Checks for collisions between shapes in each layer
	 * 
	 * @param delta Delta passed in from the scene
	 */
	public void checkForCollisions(float delta)
	{
		updateLists();

		// Go through each layer
		for (int layerIndex = 0; layerIndex < layers.size(); layerIndex++)
		{
			List<CollisionShape> layer = layers.valueAt(layerIndex);

			// collide tests
			for (int i = 0; i < layer.size(); i++)
			{
				// get the next thing to collide with (CollisionShape i)
				CollisionShape csi = layer.get(i);

				for (int j = i + 1; j < layer.size(); j++)
				{
					// get a different thing to collide with (CollisionShape j)
					CollisionShape csj = layer.get(j);

					// test for collisions
					csi.testCollision(csj, delta);
				}
			}
		}
	}

	/**
	 * Gets the count of children in the specified layer
	 * 
	 * @param layer
	 *            Layer to be looked at
	 * @return The number of children in the layer
	 */
	public int getChildCount(int layer)
	{
		return layers.get(layer).size();
	}

	/**
	 * Gets all the shapes in the specified layer
	 * 
	 * @param layer
	 *            Layer to be looked at
	 * @return The list of children in the layer
	 */
	public List<CollisionShape> getChildren(int layer)
	{
		return layers.get(layer);
	}

	/**
	 * Get the scene that this collision manager is being used on
	 * 
	 * @return Gets the Scene associated with this CollisionManager
	 */
	public Scene getScene()
	{
		return scene;
	}

	/**
	 * Adds all the shapes from layer to the remove list to be removed
	 * 
	 * @param layer
	 *            Layer to be cleared
	 */
	public void removeAllChildren(int layer)
	{
		// if there is no layer to remove objects from, then no need to continue
		if (layers.get(layer) == null)
			return;

		// get the layer that holds the item being removed
		List<CollisionShape> remove = removeList.get(layer);

		// if the remove list has net yet been made for that layer, create it
		if (remove == null)
		{
			removeList.put(layer, new ArrayList<CollisionShape>());
			remove = removeList.get(layer);
		}

		remove.addAll(layers.get(layer));

		// add this layer to the updatelayer list, so this layer gets updated
		if (!updateLayers.contains(layer))
			updateLayers.add(layer);
	}

	/**
	 * Removes the child from the specified layer
	 * 
	 * @param child
	 *            Child to be removed
	 * @param layerNum
	 *            Layer to remove child from
	 */
	public void removeChild(CollisionShape child, int layerNum)
	{
		if (child == null)
			return;

		// get the layer that holds the item being removed
		List<CollisionShape> remove = removeList.get(layerNum);

		// if the remove list has net yet been made for that layer, create it
		if (remove == null)
		{
			removeList.put(layerNum, new ArrayList<CollisionShape>());
			remove = removeList.get(layerNum);
		}

		// add item to remove list to be removed later
		remove.add(child);

		// add this layer to the updatelayer list, so this layer gets updated
		if (!updateLayers.contains(layerNum))
			updateLayers.add(layerNum);
	}

	/**
	 * Updates all the lists in each layer
	 */
	public void updateLists()
	{
		// Go through each layer
		for (Integer layerIndex : updateLayers)
		{
			List<CollisionShape> layer = layers.get(layerIndex);
			List<CollisionShape> addLayer = addList.get(layerIndex);
			List<CollisionShape> removeLayer = removeList.get(layerIndex);

			// if this layer has not been made yet, make it
			if (layer == null)
			{
				layer = new ArrayList<CollisionShape>();
				layers.put(layerIndex, layer);
			}

			if (removeLayer != null)
			{
				// Remove all children that are in the specified layer's remove
				// list
				layer.removeAll(removeLayer);

				// Remove each shape's manager if it's this manager.
				for (CollisionShape shape : removeLayer)
				{
					if (shape.getManager() == this)
					{
						shape.setManager(null);
					}
				}

				// Clear the remove list
				removeList.clear();
			}

			if (addLayer != null)
			{
				// Add all children that are in the specified layer's add list
				layer.addAll(addLayer);

				// Set each shape's manager to this manager.
				for (CollisionShape shape : addLayer)
				{
					shape.setManager(this);
				}

				// Clear the add list
				addList.clear();
			}
		}

		updateLayers.clear();
	}
}
