package gate.sql.fetcher;

import gate.error.HierarchyException;
import gate.sql.Cursor;
import gate.type.TreeNode;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Fetches all rows as tree nodes and assembles their parent relationships.
 *
 * @param <T> node type
 * @param <I> node id type
 */
public class TreeNodeListFetcher<T extends TreeNode<T>, I> implements Fetcher<List<T>>
{

	private final Class<T> type;
	private final Function<T, I> id;
	private final Function<String, Object> context;

	/**
	 * Creates a new tree node fetcher for the specified type.
	 *
	 * @param type node type to be fetched
	 * @param id   function used to extract each node id
	 */
	public TreeNodeListFetcher(Class<T> type, Function<T, I> id)
	{
		this(type, id, ignore -> null);
	}

	/**
	 * Creates a new tree node fetcher for the specified type.
	 *
	 * <p>The {@code context} may provide a value for a property, identified by its full name,
	 * before the cursor tries to read it from the current row. Returning {@code null} means
	 * the property should be resolved normally from the cursor.</p>
	 *
	 * @param type    node type to be fetched
	 * @param id      function used to extract each node id
	 * @param context function used to provide contextual values for specific property names
	 */
	public TreeNodeListFetcher(Class<T> type, Function<T, I> id,
	                           Function<String, Object> context)
	{
		this.type = Objects.requireNonNull(type);
		this.id = Objects.requireNonNull(id);
		this.context = Objects.requireNonNull(context);
	}

	/**
	 * Fetches all rows as tree nodes.
	 *
	 * @param cursor cursor to be fetched
	 * @return root nodes of the assembled tree
	 */
	@Override
	public List<T> fetch(Cursor cursor)
	{
		var graph = cursor.getPropertyGraph(type);
		var nodes = new LinkedHashMap<I, T>();

		while (cursor.next())
		{
			var node = cursor.getEntity(graph, context);
			var nodeId = id.apply(node);
			if (nodeId == null)
				throw new HierarchyException("Null node id found while assembling tree");
			if (nodes.putIfAbsent(nodeId, node) != null)
				throw new HierarchyException("Duplicate node id found while assembling tree: " + nodeId);
		}

		for (var node : nodes.values())
		{
			var nodeParent = node.getParent();
			var nodeParentId = nodeParent != null ? id.apply(nodeParent) : null;
			if (nodeParentId != null)
			{
				var actualParent = nodes.get(nodeParentId);
				if (actualParent == null)
					throw new HierarchyException("Unknown parent id found while assembling tree: " + nodeParentId);
				node.setParent(actualParent);
			}
			else
				node.setParent(null);
		}

		return nodes.values().stream()
				.filter(TreeNode::isRoot)
				.toList();
	}
}
