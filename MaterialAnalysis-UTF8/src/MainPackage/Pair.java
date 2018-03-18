package MainPackage;
public class Pair<L,R> implements Comparable<Pair<L,R>> {

  private L left;
  private R right;

  public Pair(L left, R right) {
    this.left = left;
    this.right = right;
  }

  public L getLeft() { return left; }
  public R getRight() { return right; }
  public void setLeft(L left){ this.left = left;}
  public void setRight(R right){ this.right = right;}

	@Override
	public int compareTo(Pair<L, R> o) 
	{
		if(left instanceof Comparable<?>)
		{
			if(((Comparable<L>) left).compareTo(o.getLeft()) == 0)
			{
				if(right instanceof Comparable<?>)
					return ((Comparable<R>) right).compareTo(o.getRight());
				else
					return ((Comparable<L>) left).compareTo(o.getLeft());	
			}
			else
				return ((Comparable<L>) left).compareTo(o.getLeft());
		}
		else if(right instanceof Comparable<?>)
			return ((Comparable<R>) right).compareTo(o.getRight());
		else
			return 0;
	}
}