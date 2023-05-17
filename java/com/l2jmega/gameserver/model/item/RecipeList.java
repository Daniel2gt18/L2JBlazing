package com.l2jmega.gameserver.model.item;

import java.util.ArrayList;
import java.util.List;

import com.l2jmega.gameserver.model.holder.IntIntHolder;

/**
 * This class describes a Recipe used by Dwarf to craft Item.<br>
 * All RecipeList are made of ItemHolder (1 line of the recipe : Item-Quantity needed).
 */
public class RecipeList
{
	/** The table containing all ItemHolder (1 line of the recipe : Item-Quantity needed) of the RecipeList */
	private final List<IntIntHolder> _neededRecipeParts = new ArrayList<>();
	
	/** The Identifier of the Instance */
	private final int _id;
	
	/** The crafting level needed to use this RecipeList */
	private final int _level;
	
	/** The Identifier of the RecipeList */
	private final int _recipeId;
	
	/** The name of the RecipeList */
	private final String _recipeName;
	
	/** The crafting succes rate when using the RecipeList */
	private final int _successRate;
	
	/** The crafting MP cost of this RecipeList */
	private final int _mpCost;
	
	/** The Identifier of the Item crafted with this RecipeList */
	private final int _itemId;
	
	/** The quantity of Item crafted when using this RecipeList */
	private final int _count;
	
	/** If this a common or a dwarven recipe */
	private final boolean _isDwarvenRecipe;
	
	public RecipeList(int id, int level, int recipeId, String recipeName, int successRate, int mpCost, int itemId, int count, boolean isDwarvenRecipe)
	{
		_id = id;
		_level = level;
		_recipeId = recipeId;
		_recipeName = recipeName;
		_successRate = successRate;
		_mpCost = mpCost;
		_itemId = itemId;
		_count = count;
		_isDwarvenRecipe = isDwarvenRecipe;
	}
	
	/**
	 * Add a ItemHolder to the RecipeList (add a line Item-Quantity needed to the Recipe).
	 * @param recipe The recipe to add.
	 */
	public void addNeededRecipePart(IntIntHolder recipe)
	{
		_neededRecipeParts.add(recipe);
	}
	
	/**
	 * @return the Identifier of the Instance.
	 */
	public int getId()
	{
		return _id;
	}
	
	/**
	 * @return the crafting level needed to use this RecipeList.
	 */
	public int getLevel()
	{
		return _level;
	}
	
	/**
	 * @return the Identifier of the RecipeList.
	 */
	public int getRecipeId()
	{
		return _recipeId;
	}
	
	/**
	 * @return the name of the RecipeList.
	 */
	public String getRecipeName()
	{
		return _recipeName;
	}
	
	/**
	 * @return the crafting success rate when using the RecipeList.
	 */
	public int getSuccessRate()
	{
		return _successRate;
	}
	
	/**
	 * @return the crafting MP cost of this RecipeList.
	 */
	public int getMpCost()
	{
		return _mpCost;
	}
	
	/**
	 * @return true if the Item crafted with this RecipeList is consubable (shot, arrow,...).
	 */
	public boolean isConsumable()
	{
		// Soulshots, Spiritshots, Bss, Arrows.
		return ((_itemId >= 1463 && _itemId <= 1467) || (_itemId >= 2509 && _itemId <= 2514) || (_itemId >= 3947 && _itemId <= 3952) || (_itemId >= 1341 && _itemId <= 1345));
	}
	
	/**
	 * @return the Identifier of the Item crafted with this RecipeList.
	 */
	public int getItemId()
	{
		return _itemId;
	}
	
	/**
	 * @return the quantity of Item crafted when using this RecipeList.
	 */
	public int getCount()
	{
		return _count;
	}
	
	/**
	 * @return true if this a Dwarven recipe or false if its a Common recipe.
	 */
	public boolean isDwarvenRecipe()
	{
		return _isDwarvenRecipe;
	}
	
	/**
	 * @return the table containing all ItemHolder (1 line of the recipe : Item-Quantity needed) of the RecipeList.
	 */
	public List<IntIntHolder> getNeededRecipeParts()
	{
		return _neededRecipeParts;
	}
}