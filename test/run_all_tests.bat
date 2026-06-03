@echo off
chcp 65001 > nul
setlocal enabledelayedexpansion

echo ================================================================================
echo EJECUTANDO TESTS DE ANÁLISIS DE CÓDIGO - SonarQ_Python
echo ================================================================================
echo.

set "JAVA_CMD=java -cp ../out;../ANTLR/*;../gen Main"
set "TEST_COUNT=0"
set "SEPARATOR=────────────────────────────────────────────────────────────────────────────────"

REM Test 1: HardcodedCredentials
set /a TEST_COUNT+=1
echo.
echo %SEPARATOR%
echo TEST !TEST_COUNT!: HardcodedCredentials
echo %SEPARATOR%
echo.
echo [VULNERABLE]
%JAVA_CMD% Test_HardcodedCredentials_Vulnerable.py
echo.
echo [SEGURO]
%JAVA_CMD% Test_HardcodedCredentials_Secure.py
echo.
pause

REM Test 2: InsecureCookieConfig
set /a TEST_COUNT+=1
echo.
echo %SEPARATOR%
echo TEST !TEST_COUNT!: InsecureCookieConfig
echo %SEPARATOR%
echo.
echo [VULNERABLE]
%JAVA_CMD% Test_InsecureCookieConfig_Vulnerable.py
echo.
echo [SEGURO]
%JAVA_CMD% Test_InsecureCookieConfig_Secure.py
echo.
pause

REM Test 3: SQLInjectionConcat
set /a TEST_COUNT+=1
echo.
echo %SEPARATOR%
echo TEST !TEST_COUNT!: SQLInjectionConcat
echo %SEPARATOR%
echo.
echo [VULNERABLE]
%JAVA_CMD% Test_SQLInjectionConcat_Vulnerable.py
echo.
echo [SEGURO]
%JAVA_CMD% Test_SQLInjectionConcat_Secure.py
echo.
pause

REM Test 4: InsecureYamlLoad
set /a TEST_COUNT+=1
echo.
echo %SEPARATOR%
echo TEST !TEST_COUNT!: InsecureYamlLoad
echo %SEPARATOR%
echo.
echo [VULNERABLE]
%JAVA_CMD% Test_InsecureYamlLoad_Vulnerable.py
echo.
echo [SEGURO]
%JAVA_CMD% Test_InsecureYamlLoad_Secure.py
echo.
pause

REM Test 5: WeakHashAlgorithm
set /a TEST_COUNT+=1
echo.
echo %SEPARATOR%
echo TEST !TEST_COUNT!: WeakHashAlgorithm
echo %SEPARATOR%
echo.
echo [VULNERABLE]
%JAVA_CMD% Test_WeakHashAlgorithm_Vulnerable.py
echo.
echo [SEGURO]
%JAVA_CMD% Test_WeakHashAlgorithm_Secure.py
echo.
pause

REM Test 6: UnsafeFilePermissions
set /a TEST_COUNT+=1
echo.
echo %SEPARATOR%
echo TEST !TEST_COUNT!: UnsafeFilePermissions
echo %SEPARATOR%
echo.
echo [VULNERABLE]
%JAVA_CMD% Test_UnsafeFilePermissions_Vulnerable.py
echo.
echo [SEGURO]
%JAVA_CMD% Test_UnsafeFilePermissions_Secure.py
echo.
pause

REM Test 7: CyclomaticComplexity
set /a TEST_COUNT+=1
echo.
echo %SEPARATOR%
echo TEST !TEST_COUNT!: CyclomaticComplexity
echo %SEPARATOR%
echo.
echo [VULNERABLE]
%JAVA_CMD% Test_CyclomaticComplexity_Vulnerable.py
echo.
echo [SEGURO]
%JAVA_CMD% Test_CyclomaticComplexity_Secure.py
echo.
pause

REM Test 8: LongMethod
set /a TEST_COUNT+=1
echo.
echo %SEPARATOR%
echo TEST !TEST_COUNT!: LongMethod
echo %SEPARATOR%
echo.
echo [VULNERABLE]
%JAVA_CMD% Test_LongMethod_Vulnerable.py
echo.
echo [SEGURO]
%JAVA_CMD% Test_LongMethod_Secure.py
echo.
pause

REM Test 9: DeadCode
set /a TEST_COUNT+=1
echo.
echo %SEPARATOR%
echo TEST !TEST_COUNT!: DeadCode
echo %SEPARATOR%
echo.
echo [VULNERABLE]
%JAVA_CMD% Test_DeadCode_Vulnerable.py
echo.
echo [SEGURO]
%JAVA_CMD% Test_DeadCode_Secure.py
echo.

echo.
echo ================================================================================
echo TESTS COMPLETADOS: !TEST_COUNT! reglas analizadas
echo ================================================================================
echo.
pause

@REM Made with Bob
