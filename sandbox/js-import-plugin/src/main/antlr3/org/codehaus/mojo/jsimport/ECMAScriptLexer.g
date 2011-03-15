/*
 * Copyright 2010 Class Action P/L
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

lexer grammar ECMAScriptLexer;

options {filter=true;}

@lexer::header {
	package org.codehaus.mojo.jsimport;
	
	import java.util.ArrayList;
	import java.util.List;
}

@members {
	private int varScopeLevel = 0;
	
	private List<String> assignedGlobalVars = new ArrayList<String>();
	private List<String> unassignedGlobalVars = new ArrayList<String>();
	
	public class GAV {
		public String groupId;
		public String artifactId;
		
		public String toString() {
			return groupId + ":" + artifactId;
		}
	}
	
	private List<GAV> importGavs = new ArrayList<GAV>();
	
	public List<String> getAssignedGlobalVars() {
		return assignedGlobalVars;
	}
	
	public List<GAV> getImportGavs() {
		return importGavs;
	}
	
	public List<String> getUnassignedGlobalVars() {
		return unassignedGlobalVars;
	}
}

EXTERNAL_VAR
    :   '/*global' WS EXTERNAL_VAR_DECL (WS? ',' WS? EXTERNAL_VAR_DECL)* '*/'
    ;

fragment EXTERNAL_VAR_DECL
    :   name=ID WS? (':' WS? ('true'|'false'))? {
   			unassignedGlobalVars.add($name.text);
		}
    ;

IMPORTDOC
	:	'/**' .* IMPORT .* (IMPORT .* )* '*/'
	;
	
fragment 
IMPORT
	:	'@import' WS groupId=GAVID ':' artifactId=GAVID {
			// Note the import GAV params.
			GAV importGav = new GAV();
			importGav.groupId = $groupId.text;
			importGav.artifactId = $artifactId.text;
			importGavs.add(importGav);
		}
	;

fragment
GAVID  
	:   ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'-'|'0'..'9'|'.')*
    ;

COMMENT
	:	'/*' .* '*/'
	;
	
SL_COMMENT
	:	'//' .* '\n' 
	;

LITERAL
	:	( '/' RegexLiteral 
		| '\'' CharLiteral
		| '"' StringLiteral
		)
	;
	
fragment
RegexLiteral
    :   (   EscapeQuoteSequence
        |   ~( '\\' | '/' )        
        )* 
        '/' 
    ;

fragment
CharLiteral
    :   (   EscapeQuoteSequence 
        |   ~( '\\' | '\'' )
        )*
        '\''
    ; 

fragment
StringLiteral
    :   (   EscapeQuoteSequence
        |   ~( '\\' | '"' )        
        )* 
        '"' 
    ;

fragment
EscapeQuoteSequence 
    :   '\\' .?          
	;     

ENTER_SCOPE
	:	'{' {++varScopeLevel;}
	;
	
EXIT_SCOPE
	:	'}' {--varScopeLevel;}
	;
	
WINDOW_VAR
    :   'window.' name=ID WS? '=' .* ';' {
			assignedGlobalVars.add($name.text);
		}
    ;

GLOBAL_VAR
    :   'var' WS VAR_DECL (WS? ',' WS? VAR_DECL)* ';'
    ;

fragment VAR_DECL @init {
	value = 0;
}   :   name=ID WS? value='='? {
        	if (varScopeLevel == 0) {
	    		if ($value == '=') {
	    			assignedGlobalVars.add($name.text);
				}
		    }
		}
    ;

GLOBAL_OBJECT
    :   'function' WS OBJECT_DECL
    ;

fragment OBJECT_DECL
    :   name=ID WS? '(' .* ')' {
        	if (varScopeLevel == 0) {
    			assignedGlobalVars.add($name.text);
		    }
		}
    ;

fragment
ID  :   ('a'..'z'|'A'..'Z'|'$'|'_') ('a'..'z'|'A'..'Z'|'$'|'_'|'0'..'9')*
    ;

fragment
WS  :   (' '|'\t'|'\n')+
    ;

