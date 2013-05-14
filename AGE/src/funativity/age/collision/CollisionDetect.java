package funativity.age.collision;

/**
 * Detects collision types between collision boundaries.
 * 
 */
public class CollisionDetect
{
	/**
	 * Detects if two spheres are intersecting by finding the distance between
	 * their centers, summing their radiuses, and checking if the distance is
	 * less than the sum.
	 * 
	 * @param sphere1
	 *            first spherical collision boundary
	 * @param sphere2
	 *            second spherical collision boundary
	 * @return true if the spheres are intersecting, false otherwise
	 */
	public static boolean isIntersectSpheres(CollisionSphere sphere1,
			CollisionSphere sphere2)
	{
		float distance = CollisionShape.getDistance(sphere1, sphere2);
		float minDistance = sphere1.getRadius() + sphere2.getRadius();
		return distance < minDistance;
	}

	/**
	 * Detects if two Axis Aligned Boxes are intersecting, taking position,
	 * offset, and size into account. Returns true in all cases where the two
	 * boundaries are touching, including the case where only their edges touch.
	 * 
	 * @param box1
	 *            first AAB collision boundary
	 * @param box2
	 *            second AAB collision boundary
	 * @return true if the boxes are intersecting, false otherwise.
	 */
	public static boolean isIntersectAAB(CollisionAAB box1, CollisionAAB box2)
	{
		// combine the offsets for each box, and simulate moving box 1 to the
		// origin
		float dx = (box2.getEntity().getX() + box2.getOffset().getX())
				- (box1.getEntity().getX() + box1.getOffset().getX());
		float dy = (box2.getEntity().getY() + box2.getOffset().getY())
				- (box1.getEntity().getY() + box1.getOffset().getY());

		// enforce positive values
		dx = Math.abs(dx);
		dy = Math.abs(dy);

		// get the allowed distance apart the 2 boxes can be
		// NOTE: divide by 2 because we only care about the distance from the
		// origin of the shape
		float distX = (box2.getWidth() + box1.getWidth()) / 2f;
		float distY = (box2.getHeight() + box1.getHeight()) / 2f;

		// if the shapes are far enough apart in both directions, then there is
		// no collision
		return dx <= distX && dy <= distY;
	}

	/**
	 * Simple AABbox to circle collision (assumes sphere is a 2D circle).
	 * Returns true if and only if the two objects are touching, otherwise
	 * false.
	 * 
	 * @param box
	 *            AAB collision shape
	 * @param sphere
	 *            circle collision shape
	 * @return true if the two objects are touching
	 */
	public static boolean isIntersectAABToSphere(CollisionAAB box,
			CollisionSphere sphere)
	{
		// get box properties
		float boxX = box.getEntity().getX() + box.getOffset().getX();
		float boxY = box.getEntity().getY() + box.getOffset().getY();
		float boxHW = box.getWidth() / 2f;
		float boxHH = box.getHeight() / 2f;
		float boxL = boxX - boxHW;
		float boxR = boxX + boxHW;
		float boxT = boxY + boxHH;
		float boxB = boxY - boxHH;

		// get sphere properties (assume sphere is 2D)
		float sphereX = sphere.getEntity().getX() + sphere.getOffset().getX();
		float sphereY = sphere.getEntity().getY() + sphere.getOffset().getY();
		float sphereR = sphere.getRadius();

		// get offset in position
		float dy = sphereY - boxY;
		float dx = sphereX - boxX;
		// inline abs (faster than Math.abs)
		dy = dy < 0 ? -dy : dy;
		dx = dx < 0 ? -dx : dx;

		// case 0. the 2 objects are no where near eachother
		if (boxHW + sphereR < dx && boxHH + sphereR < dy)
			return false;

		// case 1. Sphere is inside of box (the center of sphere is inside)
		if (boxL <= sphereX && boxR >= sphereX && boxT >= sphereY
				&& boxB <= sphereY)
			return true;

		// case 2. Sphere is above, or below box
		else if (boxL < sphereX && boxR > sphereX)
		{
			float dist = sphereR + boxHH;
			if (dy <= dist)
				return true;

		}
		// case 3. Sphere is to the left or right of box
		else if (boxT > sphereY && boxB < sphereY)
		{
			float dist = sphereR + boxHW;
			if (dx <= dist)
				return true;
		}
		// case 4. Complicated case. Sphere must be in one of the corners of the
		// box.
		else
		{
			// Get the position of the circle compared to the correct corner of
			// the box
			float xCheck = sphereX - sphereX > boxX ? boxR : boxL;
			float yCheck = sphereY - sphereY > boxY ? boxT : boxB;

			float x2 = xCheck * xCheck;
			float y2 = yCheck * yCheck;

			// compare the distance from the center of the circle, to the corner
			// of the box vs the length of the radius
			if (x2 + y2 < sphereR)
			{
				return true;
			}
		}

		return false;
	}
}