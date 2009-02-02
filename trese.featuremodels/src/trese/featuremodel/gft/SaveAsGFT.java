/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.featuremodel.gft;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import trese.featuremodel.model.Feature;
import trese.featuremodel.model.FeatureConstraint;

/**
 * Save a feature tree to an output stream in the GFT format
 * 
 * @author Michiel Hendriks
 */
public class SaveAsGFT
{
	public static boolean save(Feature root, OutputStream toStream)
	{
		SaveAsGFT sag = new SaveAsGFT(root, toStream);
		return sag.save();
	}

	protected Feature root;
	protected PrintStream output;

	/**
	 * All found constraints
	 */
	protected Set<FeatureConstraint> constraints;

	/**
	 * The features that were saved. Used to filter constraints
	 */
	protected Set<Feature> features;

	protected SaveAsGFT(Feature rootFeature, OutputStream os)
	{
		constraints = new HashSet<FeatureConstraint>();
		features = new HashSet<Feature>();
		root = rootFeature;
		if (os instanceof PrintStream)
		{
			output = (PrintStream) os;
		}
		else
		{
			output = new PrintStream(os);
		}
	}

	protected boolean save()
	{
		return saveFeature(root) & saveConstraints();
	}

	protected String normalizeId(String id)
	{
		return id;
	}

	protected String normalizeString(String string)
	{
		return String.format("\"%s\"", string.replaceAll("\"", ""));
	}

	protected String getFeatureDesc(Feature feature)
	{
		String desc = feature.getDescription();
		if (desc == null || desc.isEmpty())
		{
			desc = feature.getId();
		}
		return normalizeString(desc);
	}

	/**
	 * @param feature
	 * @return
	 */
	protected boolean saveFeature(Feature feature)
	{
		boolean res = true;
		if (feature == root)
		{
			output.print("f_tree");
		}
		else
		{
			output.print(normalizeId(feature.getId()));
		}
		output.print(" = ");
		StringBuilder g1 = null;
		StringBuilder g2 = null;
		switch (feature.getGroupRelation())
		{
			case ALTERNATIVE:
				output.print("Xor ");
				g1 = new StringBuilder();
				for (Feature f : feature.getSubFeatures())
				{
					if (g1.length() > 0)
					{
						g1.append(',');
					}
					g1.append(normalizeId(f.getId()));
				}
				break;
			case OR:
				output.print("Or ");
				g1 = new StringBuilder();
				for (Feature f : feature.getSubFeatures())
				{
					if (g1.length() > 0)
					{
						g1.append(',');
					}
					g1.append(normalizeId(f.getId()));
				}
				break;
			case NONE:
				output.print("MandOpt ");
				g1 = new StringBuilder();
				g2 = new StringBuilder();
				for (Feature f : feature.getSubFeatures())
				{
					StringBuilder target = null;
					switch (f.getRequirement())
					{
						case MANDATORY:
							target = g1;
							break;
						case OPTIONAL:
							target = g2;
							break;
						default:
							return false;
					}
					if (target.length() > 0)
					{
						target.append(',');
					}
					target.append(normalizeId(f.getId()));
				}
				break;
			default:
				return false;
		}
		output.print(getFeatureDesc(feature));
		if (g1 != null)
		{
			output.print(" [");
			output.print(g1.toString());
			output.print(']');
		}
		if (g2 != null)
		{
			output.print(" [");
			output.print(g2.toString());
			output.print(']');
		}
		output.print('\n');
		features.add(feature);
		constraints.addAll(feature.getConstraints());
		for (Feature f : feature.getSubFeatures())
		{
			res &= saveFeature(f);
		}
		return res;
	}

	/**
	 * @return
	 */
	private boolean saveConstraints()
	{
		boolean res = true;
		Set<String> actc = new HashSet<String>();
		StringBuffer ctlst = new StringBuffer();
		for (FeatureConstraint fc : constraints)
		{
			if (features.contains(fc.getLHS()) && features.contains(fc.getRHS()))
			{
				String stringConstr = saveConstraint(fc);
				if (stringConstr == null)
				{
					return false;
				}
				if (actc.add(stringConstr))
				{
					if (ctlst.length() > 0)
					{
						ctlst.append(',');
					}
					ctlst.append(getConstraintId(fc));
				}
			}
		}
		if (actc.isEmpty())
		{
			return true;
		}
		output.print("constraints = [");
		output.print(ctlst);
		output.print("]\n");
		for (String fc : actc)
		{
			output.print(fc);
		}
		return res;
	}

	/**
	 * @param fc
	 * @return
	 */
	protected Object getConstraintId(FeatureConstraint fc)
	{
		return String.format("c%d", fc.hashCode());
	}

	/**
	 * @param fc
	 * @return
	 */
	protected String saveConstraint(FeatureConstraint constraint)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getConstraintId(constraint));
		sb.append(" = ");
		switch (constraint.getType())
		{
			case EXCLUDES:
				sb.append("Excludes ");
				break;
			case REQUIRES:
				sb.append("Requires ");
				break;
			default:
				return null;
		}
		sb.append(getFeatureDesc(constraint.getLHS()));
		sb.append(' ');
		sb.append(getFeatureDesc(constraint.getRHS()));
		sb.append('\n');
		return sb.toString();
	}
}
