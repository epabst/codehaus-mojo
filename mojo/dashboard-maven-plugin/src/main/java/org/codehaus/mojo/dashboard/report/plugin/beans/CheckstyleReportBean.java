package org.codehaus.mojo.dashboard.report.plugin.beans;

/*
 * Copyright 2006 David Vicente
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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * <ul>
 * <li>Add "CheckStyle Violations" graph written by <a href="mailto:srivollet@objectif-informatique.fr">Sylvain
 * Rivollet</a></li>
 * </ul>
 * 
 * @author <a href="dvicente72@gmail.com">David Vicente</a>
 * 
 */
public class CheckstyleReportBean extends AbstractReportBean
{
    /**
     * 
     */
    private int nbClasses;

    /**
     * 
     */
    private int nbInfos;

    /**
     * 
     */
    private int nbWarnings;

    /**
     * 
     */
    private int nbErrors;

    /**
     * 
     */
    private int nbTotal;

    /**
     * List:CheckstyleError Fixes MOJO-679. Written by <a href="srivollet@objectif-informatique.fr">Sylvain Rivollet</a>.
     */
    private List errors;

    /**
     * Default constructor
     * 
     */
    public CheckstyleReportBean()
    {
        this.errors = new ArrayList();
    }

    /**
     * 
     * @param dateGeneration
     */
    public CheckstyleReportBean( Date dateGeneration )
    {
        super( dateGeneration );
        this.errors = new ArrayList();
    }

    /**
     * 
     * @return int
     */
    public int getNbClasses()
    {
        return nbClasses;
    }

    /**
     * 
     * @param nbClasses
     */
    public void setNbClasses( int nbClasses )
    {
        this.nbClasses = nbClasses;
    }

    /**
     * 
     * @return
     */
    public int getNbErrors()
    {
        return nbErrors;
    }

    /**
     * 
     * @param nbErrors
     */
    public void setNbErrors( int nbErrors )
    {
        this.nbErrors = nbErrors;
    }

    /**
     * 
     * @return
     */
    public int getNbInfos()
    {
        return nbInfos;
    }

    /**
     * 
     * @param nbInfos
     */
    public void setNbInfos( int nbInfos )
    {
        this.nbInfos = nbInfos;
    }

    /**
     * 
     * @return
     */
    public int getNbTotal()
    {
        return nbTotal;
    }

    /**
     * 
     * @param nbTotal
     */
    public void setNbTotal( int nbTotal )
    {
        this.nbTotal = nbTotal;
    }

    /**
     * 
     * @return
     */
    public int getNbWarnings()
    {
        return nbWarnings;
    }

    /**
     * 
     * @param nbWarnings
     */
    public void setNbWarnings( int nbWarnings )
    {
        this.nbWarnings = nbWarnings;
    }

    /**
     * get the checkstyleError list. Fixes MOJO-679 . Written by <a
     * href="mailto:srivollet@objectif-informatique.fr">Sylvain Rivollet</a>.
     * 
     * @return CheckstyleError list
     */
    public List getErrors()
    {
        return this.errors;
    }

    /**
     * 
     * Fixes MOJO-679 . Written by <a href="mailto:srivollet@objectif-informatique.fr">Sylvain Rivollet</a>.
     * 
     * @param errorsList
     *            CheckstyleError list
     */
    public void setErrors( List errorsList )
    {
        this.errors = errorsList;
    }

    /**
     * 
     * @param error
     */
    public void addError( CheckstyleError error )
    {
        if ( this.errors.isEmpty() )
        {
            this.errors.add( error );
        }
        else
        {
            if ( ( this.errors.contains( error ) ) )
            {

                int index = this.errors.indexOf( error );
                // Fixes MOJO-814. Correction of percentage and rendering bug on "Show Checkstyle categories"
                int nbIter = ( (CheckstyleError) this.errors.get( index ) ).getNbIteration();
                ( (CheckstyleError) this.errors.get( index ) ).setNbIteration( nbIter + error.getNbIteration() );
            }
            else
            {
                this.errors.add( error );
            }
        }
    }

    /**
     * Fixes MOJO-679 . Written by <a href="mailto:srivollet@objectif-informatique.fr">Sylvain Rivollet</a>.
     * 
     * @param error
     */
    public void addAllError( List errorsList )
    {
        if ( this.errors.isEmpty() )
        {
            Iterator iter = errorsList.iterator();
            while ( iter.hasNext() )
            {
                CheckstyleError error = (CheckstyleError) iter.next();
                this.errors.add( (CheckstyleError) ( error.clone() ) );
            }
        }
        else
        {
            Iterator iter = errorsList.iterator();
            while ( iter.hasNext() )
            {
                CheckstyleError error = (CheckstyleError) iter.next();
                this.addError( (CheckstyleError) ( error.clone() ) );
            }
        }
    }

    /**
     * Fixes MOJO-679 . Written by <a href="mailto:srivollet@objectif-informatique.fr">Sylvain Rivollet</a>.
     * 
     * @param dashboardReport
     */
    public void merge( IDashBoardReportBean dashboardReport )
    {
        if ( dashboardReport != null && dashboardReport instanceof CheckstyleReportBean )
        {
            this.nbClasses = this.nbClasses + ( (CheckstyleReportBean) dashboardReport ).getNbClasses();

            this.nbInfos = this.nbInfos + ( (CheckstyleReportBean) dashboardReport ).getNbInfos();

            this.nbWarnings = this.nbWarnings + ( (CheckstyleReportBean) dashboardReport ).getNbWarnings();

            this.nbErrors = this.nbErrors + ( (CheckstyleReportBean) dashboardReport ).getNbErrors();

            this.nbTotal = this.nbTotal + ( (CheckstyleReportBean) dashboardReport ).getNbTotal();
            // error management . Fixes MOJO-679 .
            // Written by <a href="mailto:srivollet@objectif-informatique.fr">Sylvain Rivollet</a>.
            this.addAllError( ( (CheckstyleReportBean) dashboardReport ).getErrors() );
        }
    }

    /**
     * 
     */
    protected Object clone()
    {
        CheckstyleReportBean clone = new CheckstyleReportBean( this.getDateGeneration() );
        clone.setNbClasses( this.nbClasses );
        clone.setNbInfos( this.nbInfos );
        clone.setNbWarnings( this.nbWarnings );
        clone.setNbErrors( this.nbErrors );
        clone.setNbTotal( this.nbTotal );
        clone.addAllError( this.getErrors() );
        return clone;
    }

    public double getPercentInfos()
    {
        return getPercentageValue( this.nbInfos, this.nbTotal );
    }

    public double getPercentErrors()
    {
        return getPercentageValue( this.nbErrors, this.nbTotal );
    }

    public double getPercentWarnings()
    {
        return getPercentageValue( this.nbWarnings, this.nbTotal );
    }

}
