package tarau.jinni;

/**
 * Always fails
 */
class fail_ extends ConstBuiltin
{
	fail_()
	{
		super("fail");
	}

	@Override
	public int exec(Prog p)
	{
		return 0;
	}
}
