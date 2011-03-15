package org.codehaus.mojo.dashboard.report.plugin.beans;

/*
 * Copyright 2007 Matthew Beermann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.text.NumberFormat;
import java.util.Date;

/**
 * @author <a href="mbeerman@yahoo.com">Matthew Beermann</a>
 */
public class CloverReportBean extends AbstractReportBean
{
    private int conditionals, statements, methods, elements;

    private int coveredConditionals, coveredStatements, coveredMethods, coveredElements;

    private static final NumberFormat FormatPercent = NumberFormat.getPercentInstance();
    static
    {
        FormatPercent.setMaximumFractionDigits( 1 );
    }

    /**
     * Construct a new CloverReportBean against the given project.
     */
    public CloverReportBean()
    {

    }

    /**
     * 
     * @param dateGeneration
     */
    public CloverReportBean( Date dateGeneration )
    {
        super( dateGeneration );

    }

    /**
     * @return the conditionals
     */
    public int getConditionals()
    {
        return conditionals;
    }

    /**
     * @return a formatted version of the conditionals
     */
    public String getConditionalsLabel()
    {
        return getPercentage( coveredConditionals, conditionals ) + " (" + coveredConditionals + " / " + conditionals
                        + ")";
    }

    /**
     * @param conditionals
     *            the conditionals to set
     */
    public void setConditionals( int conditionals )
    {
        this.conditionals = conditionals;
    }

    /**
     * @return the coveredConditionals
     */
    public int getCoveredConditionals()
    {
        return coveredConditionals;
    }

    /**
     * @param coveredConditionals
     *            the coveredConditionals to set
     */
    public void setCoveredConditionals( int coveredConditionals )
    {
        this.coveredConditionals = coveredConditionals;
    }

    /**
     * @return the coveredElements
     */
    public int getCoveredElements()
    {
        return coveredElements;
    }

    /**
     * @param coveredElements
     *            the coveredElements to set
     */
    public void setCoveredElements( int coveredElements )
    {
        this.coveredElements = coveredElements;
    }

    /**
     * @return the coveredMethods
     */
    public int getCoveredMethods()
    {
        return coveredMethods;
    }

    /**
     * @param coveredMethods
     *            the coveredMethods to set
     */
    public void setCoveredMethods( int coveredMethods )
    {
        this.coveredMethods = coveredMethods;
    }

    /**
     * @return the coveredStatements
     */
    public int getCoveredStatements()
    {
        return coveredStatements;
    }

    /**
     * @param coveredStatements
     *            the coveredStatements to set
     */
    public void setCoveredStatements( int coveredStatements )
    {
        this.coveredStatements = coveredStatements;
    }

    /**
     * @return the elements
     */
    public int getElements()
    {
        return elements;
    }

    /**
     * @return a formatted version of the elements
     */
    public String getElementsLabel()
    {
        return getPercentage( coveredElements, elements ) + " (" + coveredElements + " / " + elements + ")";
    }

    /**
     * @param elements
     *            the elements to set
     */
    public void setElements( int elements )
    {
        this.elements = elements;
    }

    /**
     * @return the methods
     */
    public int getMethods()
    {
        return methods;
    }

    /**
     * @return a formatted version of the methods
     */
    public String getMethodsLabel()
    {
        return getPercentage( coveredMethods, methods ) + " (" + coveredMethods + " / " + methods + ")";
    }

    /**
     * @param methods
     *            the methods to set
     */
    public void setMethods( int methods )
    {
        this.methods = methods;
    }

    /**
     * @return the statements
     */
    public int getStatements()
    {
        return statements;
    }

    /**
     * @return a formatted version of the statements
     */
    public String getStatementsLabel()
    {
        return getPercentage( coveredStatements, statements ) + " (" + coveredStatements + " / " + statements + ")";
    }

    /**
     * @param statements
     *            the statements to set
     */
    public void setStatements( int statements )
    {
        this.statements = statements;
    }

    /**
     * @param dashboardReport
     */
    public void merge( IDashBoardReportBean dashboardReport )
    {
        if ( dashboardReport != null && dashboardReport instanceof CloverReportBean )
        {
            conditionals += ( (CloverReportBean) dashboardReport ).getConditionals();

            statements += ( (CloverReportBean) dashboardReport ).getStatements();

            methods += ( (CloverReportBean) dashboardReport ).getMethods();

            elements += ( (CloverReportBean) dashboardReport ).getElements();

            coveredConditionals += ( (CloverReportBean) dashboardReport ).getCoveredConditionals();

            coveredStatements += ( (CloverReportBean) dashboardReport ).getCoveredStatements();

            coveredMethods += ( (CloverReportBean) dashboardReport ).getCoveredMethods();

            coveredElements += ( (CloverReportBean) dashboardReport ).getCoveredElements();
        }
    }

    private String getPercentage( int numerator, int denominator )
    {
        String percent = "0%";
        if ( denominator > 0 )
        {
            double percentage = numerator / (double) denominator;
            percent = FormatPercent.format( percentage );
        }
        return percent;
    }

    public double getPercentCoveredStatements()
    {
        return getPercentageValue( this.coveredStatements, this.statements );
    }

    public double getPercentCoveredConditionals()
    {
        return getPercentageValue( this.coveredConditionals, this.conditionals );
    }

    public double getPercentCoveredElements()
    {
        return getPercentageValue( this.coveredElements, this.elements );
    }

    public double getPercentCoveredMethods()
    {
        return getPercentageValue( this.coveredMethods, this.methods );
    }

    public double getPercentUnCoveredStatements()
    {
        int uncovered = this.statements - this.coveredStatements;
        return getPercentageValue( uncovered, this.statements );
    }

    public double getPercentUnCoveredConditionals()
    {
        int uncovered = this.conditionals - this.coveredConditionals;
        return getPercentageValue( uncovered, this.conditionals );
    }

    public double getPercentUnCoveredElements()
    {
        int uncovered = this.elements - this.coveredElements;
        return getPercentageValue( uncovered, this.elements );
    }

    public double getPercentUnCoveredMethods()
    {
        int uncovered = this.methods - this.coveredMethods;
        return getPercentageValue( uncovered, this.methods );
    }
}
