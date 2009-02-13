package tarau.jinni;

/**
 * Builds an iterator from a list
 */
class ListSource extends JavaSource
{
	ListSource(Const Xs, Prog p)
	{
		super(Copier.ConsToVector(Xs), p);
	}
}
