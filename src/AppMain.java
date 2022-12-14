import jdk.nashorn.internal.scripts.JO;
import model.*;
import repository.*;

import javax.swing.*;

import static java.lang.Integer.parseInt;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

public class AppMain {
    public static void main(String[] args) throws Exception {
        callLogin();
    }

    private static void callLogin() throws Exception {
        String username = JOptionPane.showInputDialog(null, "Informe o login do usuário ",
                "Login Sistema", 3);
        String password = JOptionPane.showInputDialog(null, "Informe a senha do usuário " + username,
                "Login Sistema", 3);
        if (username != null && password != null)
            checkUser(username, password);
    }

    private static void checkUser(String username, String password) throws Exception {
        User user = getUserDAO().checkUserAuth(username, password);
        if (user.getId() != null) {
            callMenuOptions();
        }else{
            JOptionPane.showMessageDialog(null, "Usuário ou senha inválidos!", "Login Sistema", JOptionPane.INFORMATION_MESSAGE);
            callLogin();
        }
    }

    private static void callMenuOptions() throws Exception {

        String[] optionsMenu = {"Entidades", "Relatórios", "Sair"};
        int menuOptions = JOptionPane.showOptionDialog(null, "Selecione uma opção :",
                "Menu Opções ",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, optionsMenu, optionsMenu[0]);
        switch (menuOptions) {
            case 0:
                callMenuEntities();
                break;
            case 1:
                callMenuReports();
                break;
            case 2:
                callLogin();
                break;
        }
    }

    private static void callMenuEntities() throws Exception {

        String[] optionsMenuEntity = {"Usuários", "Setores", "Exercícios", "Orçamentos", "Tipos Orçamentos", "Voltar"};
        int menuEntity = JOptionPane.showOptionDialog(null, "Selecione uma entidade para mais ações:",
                "Menu entidades ",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, optionsMenuEntity, optionsMenuEntity[0]);
        switch (menuEntity) {
            case 0:
                callMenuUsers();
                callMenuEntities();
                break;
            case 1:
                callMenuSectors();
                callMenuEntities();
                break;
            case 2:
                callMenuExercises();
                callMenuEntities();
                break;
            case 3:
                callMenuBudgets();
                callMenuEntities();
                break;
            case 4:
                callMenuTypesBudgets();
                callMenuEntities();
                break;
            case 5:
                callMenuOptions();
                break;
        }
    }

    public static void callMenuReports() throws Exception {

        String[] optionsMenuEntity = {"Usuários", "Setores", "Exercícios", "Orçamentos", "Tipos Orçamentos", "Voltar"};
        int menuEntity = JOptionPane.showOptionDialog(null, "Selecione uma entidade para gerar o relatório: ",
                "Menu relatórios ",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, optionsMenuEntity, optionsMenuEntity[0]);
        switch (menuEntity) {
            case 0:
                callUsersReports();
                break;
            case 1:
                callSectorReports();
                break;
            case 2:
                callExercisesReports();
                break;
            case 3:
                callBudgetsReports();
                break;
            case 4:
                callTypesBudgetsReports();
                break;
            case 5:
                callMenuOptions();
                break;
        }
    }

    private static void callUsersReports() throws SQLException, ClassNotFoundException {
        List<User> users = getUserDAO().searchAll();
        ReportUserForm.emitirRelatorio(users);
    }

    private static void callSectorReports() throws SQLException, ClassNotFoundException {
        List<Sector> sectors = getSectorDAO().searchAll();
        ReportSectorForm.emitirRelatorio(sectors);
    }

    private static void callExercisesReports() throws SQLException, ClassNotFoundException {
        List<Exercise> exercises = getExerciseDAO().searchAll();
        ReportExerciseForm.emitirRelatorio(exercises);
    }

    private static void callBudgetsReports() throws SQLException, ClassNotFoundException {
        List<Budget> budgets = getBudgetDAO().searchAll();
        ReportBudgetForm.emitirRelatorio(budgets);
    }

    private static void callTypesBudgetsReports() throws SQLException, ClassNotFoundException {
        List<BudgetType> budgetTypes = getBudgetTypeDAO().searchAll();
        ReportBudgetTypeForm.emitirRelatorio(budgetTypes);
    }

    private static void callMenuUsers() throws Exception {
        String[] optionsMenuUsers = {"Novo", "Editar", "Excluir", "Voltar"};
        int menuUsers = JOptionPane.showOptionDialog(null, "Selecione uma ação: ",
                "Menu Usuários",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, optionsMenuUsers, optionsMenuUsers[0]);
        User user = null;
        switch (menuUsers) {
            case 0:
                //novo usuário
                user = callCreateUser();
                if (user != null)
                    getUserDAO().save(user);
                break;
            case 1:
                user = selectUser();
                user = callUpdateUser(user);

                if (user != null)
                    getUserDAO().save(user);

                callMenuUsers();
                break;
            case 2:
                user = selectUser();
                getUserDAO().remove(user);

                callMenuSectors();
                break;
            case 3:
                callMenuEntities();
                break;
        }
    }

    private static User callCreateUser() throws SQLException, ClassNotFoundException{
        User user = new User();
        String name = JOptionPane.showInputDialog(null, "Informe o nome do usuário",
                "Cadastro Usuário", 3);

        String userName = JOptionPane.showInputDialog(null, "Informe o login que o usuário irá utilizar",
                "Cadastro Usuário", 3);

        String password = JOptionPane.showInputDialog(null, "Informe a senha do usuário",
                "Cadastro Usuário", 3);

        Object[] arrayType = UserType.getEnumArray();
        Object auxType = JOptionPane.showInputDialog(null, "Informe o tipo do usuário", "Cadastro Usuário", JOptionPane.QUESTION_MESSAGE, null, arrayType, arrayType[1]);
        Integer type = UserType.getEnumIntValue(auxType);

        Object[] arraySectors = getSectorDAO().searchAllReturnArray();
        Object auxSector = JOptionPane.showInputDialog(null, "Informe o setor do usuário", "Cadastro Usuário", JOptionPane.QUESTION_MESSAGE, null, arraySectors, arraySectors[0]);
        List<Sector> sectors = getSectorDAO().searchByName((String) auxSector);
        Sector sector = sectors.get(0);

        user.setName(name);
        user.setUsername(userName);
        user.setSector(sector);
        user.setType(type);
        user.setActive(1);
        user.setPassword(password);
        user.setCreated(LocalDateTime.now());
        user.setModified(LocalDateTime.now());

        return user;
    }

    private static User callUpdateUser(User user) throws SQLException, ClassNotFoundException{
        String name = JOptionPane.showInputDialog(null, "Informe o novo nome do usuário", user.getName());
        String password = JOptionPane.showInputDialog(null, "Informe a nova senha", user.getPassword());

        Object[] arrayType = UserType.getEnumArray();
        Object auxType = JOptionPane.showInputDialog(null, "Informe o novo tipo do usuário", "Cadastro Usuário", JOptionPane.QUESTION_MESSAGE, null, arrayType, arrayType[1]);
        Integer type = UserType.getEnumIntValue(auxType);

        Object[] options = Active.getEnumArray();
        Object activeAux = JOptionPane.showInputDialog(null, "Informe se o usuário está ativo", null, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
        Integer active = Active.getEnumIntValue(activeAux);

        Object[] arraySectors = getSectorDAO().searchAllReturnArray();
        Object auxSector = JOptionPane.showInputDialog(null, "Informe o setor do usuário", "Cadastro Usuário", JOptionPane.QUESTION_MESSAGE, null, arraySectors, arraySectors[0]);
        List<Sector> sectors = getSectorDAO().searchByName((String) auxSector);
        Sector sector = sectors.get(0);

        user.setName(name);
        user.setPassword(password);
        user.setType(type);
        user.setActive(active);
        user.setSector(sector);
        user.setModified(LocalDateTime.now());

        return user;
    }

    private static User selectUser() throws SQLException, ClassNotFoundException {
        Object[] selectionValues = getUserDAO().searchAllReturnArray();
        String initialSelection = (String) selectionValues[0];
        Object selection = JOptionPane.showInputDialog(null, "Selecione um usuário",
                null, JOptionPane.QUESTION_MESSAGE, null, selectionValues, initialSelection);
        List<User> users = getUserDAO().searchByName((String) selection);
        return users.get(0);
    }

    private static void callMenuSectors() throws Exception {
        String[] optionsMenuSectors = {"Novo", "Editar", "Excluir", "Voltar"};
        int menuSectors = JOptionPane.showOptionDialog(null, "Selecione uma ação para Setores",
                "Menu Setores ",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, optionsMenuSectors, optionsMenuSectors[0]);
        Sector sector = null;
        switch (menuSectors) {
            case 0: //novo
                sector = callCreateSector();

                if (sector != null)
                    getSectorDAO().save(sector);

                callMenuSectors();
                break;
            case 1: //editar
                sector = selectSector();
                sector = callUpdateSector(sector);

                if (sector != null)
                    getSectorDAO().save(sector);

                callMenuSectors();
                break;
            case 2: //excluir
                sector = selectSector();
                getSectorDAO().remove(sector);

                callMenuSectors();
                break;
            case 3: //voltar
                callMenuEntities();
                break;
        }
    }

    private static Sector callCreateSector(){
        Sector sector = new Sector();

        String name = JOptionPane.showInputDialog(null, "Informe o nome do Setor",
                "Cadastro Setor", JOptionPane.QUESTION_MESSAGE);

        sector.setName(name);
        sector.setActive(1);
        sector.setCreated(LocalDateTime.now());
        sector.setModified(LocalDateTime.now());

        return sector;
    }

    private static Sector callUpdateSector(Sector sector){

        String name = JOptionPane.showInputDialog(null, "Informe o nome do Setor: ",
                sector.getName());

        Object[] options = Active.getEnumArray();
        Object activeAux = JOptionPane.showInputDialog(null, "Informe se o Setor está ativo: ", null, JOptionPane.QUESTION_MESSAGE, null, options, options[sector.getActive()]);
        Integer active = Active.getEnumIntValue(activeAux);

        sector.setName(name);
        sector.setActive(active);
        sector.setModified(LocalDateTime.now());

        return sector;
    }

    private static Sector selectSector() throws SQLException, ClassNotFoundException {
        Object[] selectionValues = getSectorDAO().searchAllReturnArray();
        String initialSelection = (String) selectionValues[0];
        Object selection = JOptionPane.showInputDialog(null, "Selecione um setor",
                null, JOptionPane.QUESTION_MESSAGE, null, selectionValues, initialSelection);
        List<Sector> sectors = getSectorDAO().searchByName((String) selection);
        return sectors.get(0);
    }

    private static void callMenuExercises() throws Exception {
        String[] optionsMenuExercises = {"Novo", "Editar", "Excluir", "Voltar"};
        int menuExercises = JOptionPane.showOptionDialog(null, "Selecione uma ação para Exercícios",
                "Menu Exercícios ",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, optionsMenuExercises, optionsMenuExercises[0]);
        Exercise exercise;
        switch (menuExercises) {
            case 0: //novo
                exercise = callCreateExercise();
                getExerciseDAO().save(exercise);
                callMenuExercises();
                break;
            case 1: //editar
                exercise = selectExercise();
                exercise = callUpdateExercise(exercise);

                if (exercise != null)
                    getExerciseDAO().save(exercise);

                callMenuExercises();
                break;
            case 2: //excluir
                exercise = selectExercise();
                getExerciseDAO().remove(exercise);

                callMenuExercises();
                break;
            case 3:
                callMenuEntities();
                break;
        }

    }

    private static Exercise callCreateExercise() throws SQLException, ClassNotFoundException {
        Exercise exercise = new Exercise();

        Integer year = Integer.valueOf(JOptionPane.showInputDialog(null, "Informe o ano do Exercício",
                "Cadastro Exercício", JOptionPane.QUESTION_MESSAGE));

        Object[] arrayStatus = ExerciseStatus.getEnumArray();

        Object auxStatus = JOptionPane.showInputDialog(null, "Informe o status do Setor", null, JOptionPane.QUESTION_MESSAGE, null, arrayStatus, arrayStatus[2]);
        Integer status = ExerciseStatus.getEnumIntValue(auxStatus);
        
        Object[] budgets = getBudgetDAO().searchAllOnlyWithName();

        Object selectionBudget = JOptionPane.showInputDialog(null, "Informe o orçamento do exercício: ", "Cadastro de exercício", JOptionPane.QUESTION_MESSAGE, null, budgets, budgets[0]);
        List<Budget> auxBudgets = getBudgetDAO().searchByName((String) selectionBudget);
        Budget budget = auxBudgets.get(0);

        exercise.setYear(year);
        exercise.setStatus(status);
        exercise.setActive(1);
        exercise.setCreated(LocalDateTime.now());
        exercise.setModified(LocalDateTime.now());
        exercise.setBudget(budget);

        return exercise;
    }

    private static Exercise callUpdateExercise(Exercise exercise){

        Integer year = Integer.valueOf(JOptionPane.showInputDialog(null, "Informe o ano do Exercício: ",
                exercise.getYear()));

        Object[] arrayStatus = ExerciseStatus.getEnumArray();

        Object auxStatus = JOptionPane.showInputDialog(null, "Informe o status do Setor: ", null, JOptionPane.QUESTION_MESSAGE, null, arrayStatus, arrayStatus[exercise.getStatus()]);
        Integer status = ExerciseStatus.getEnumIntValue(auxStatus);

        Object[] optionsActive = Active.getEnumArray();
        Object activeAux = JOptionPane.showInputDialog(null, "Informe o status do Exercício: ", null, JOptionPane.QUESTION_MESSAGE, null, optionsActive, optionsActive[exercise.getActive()]);
        Integer active = Active.getEnumIntValue(activeAux);

        exercise.setYear(year);
        exercise.setStatus(status);
        exercise.setActive(active);
        exercise.setModified(LocalDateTime.now());

        return exercise;
    }

    private static Exercise selectExercise() throws SQLException, ClassNotFoundException {
        Object[] selectionValues = getExerciseDAO().searchAllReturnArray();
        String initialSelection = String.valueOf(selectionValues[0]);
        Object selection = JOptionPane.showInputDialog(null, "Selecione um Exercício: ",
                null, JOptionPane.QUESTION_MESSAGE, null, selectionValues, initialSelection);
        List<Exercise> exercises = getExerciseDAO().searchByYear((Integer) selection);
        return exercises.get(0);
    }

            private static void callMenuBudgets() throws Exception {
                String[] optionsMenuBudgets = {"Novo", "Editar", "Excluir", "Voltar"};
                int menuBudgets = JOptionPane.showOptionDialog(null, "Selecione uma ação para Orçamentos",
                        "Menu Orçamentos",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, optionsMenuBudgets, optionsMenuBudgets[0]);
                Budget budget;
                switch (menuBudgets) {
                    case 0: //novo
                        budget = callCreateBudget();
                        getBudgetDAO().save(budget);
                        callMenuBudgets();
                        break;
                    case 1: //editar
                        budget = selectBudget();
                        budget = callUpdateBudget(budget);

                        if (budget != null)
                            getBudgetDAO().save(budget);

                        callMenuTypesBudgets();
                        break;
                    case 2: //excluir
                        budget = selectBudget();
                        getBudgetDAO().remove(budget);

                        callMenuBudgets();
                        break;
                    case 3:
                        callMenuEntities();
                        break;
                }

            }

            private static Budget callCreateBudget() throws SQLException, ClassNotFoundException {
                Budget budget = new Budget();

                String name = JOptionPane.showInputDialog(null, "Informe o nome do orçamento: ", "Cadastro orçamento", JOptionPane.QUESTION_MESSAGE);
                String item = JOptionPane.showInputDialog(null, "Informe o item do orçamento: ", "Cadastro orçamento", JOptionPane.QUESTION_MESSAGE);
                Integer qnt = Integer.parseInt(JOptionPane.showInputDialog(null, "Informe a quantidade de itens: ", "Cadastro orçamento", JOptionPane.QUESTION_MESSAGE));
                Double untVal = Double.parseDouble(JOptionPane.showInputDialog(null, "Informe o valor unitário do item: ", "Cadastro orçamento", JOptionPane.QUESTION_MESSAGE));

                Object[] arrayStatus = BudgetStatus.getEnumArray();

                Object auxStatus = JOptionPane.showInputDialog(null, "Informe o status do orçamento: ", null, JOptionPane.QUESTION_MESSAGE, null, arrayStatus, arrayStatus[2]);
                Integer status = BudgetStatus.getEnumIntValue(auxStatus);

                Object[] sectors = getSectorDAO().searchAllOnlyWithName();

                Object selectionSetor = JOptionPane.showInputDialog(null, "Informe o setor do orçamento: ", "Cadastro de orçamento", JOptionPane.QUESTION_MESSAGE, null, sectors, sectors[0]);
                List<Sector> auxSectors = getSectorDAO().searchByName((String) selectionSetor);
                Sector sector = auxSectors.get(0);

                Object[] budgetTypes = getBudgetTypeDAO().searchAllOnlyWithName();

                Object selectionBudgetType = JOptionPane.showInputDialog(null, "Informe o tipo do orçamento: ", "Cadastro de orçamento", JOptionPane.QUESTION_MESSAGE, null, budgetTypes, budgetTypes[0]);
                List<BudgetType> auxBudgetType = getBudgetTypeDAO().searchByName((String) selectionBudgetType);
                BudgetType budgetType = auxBudgetType.get(0);

                budget.setName(name);
                budget.setItem(item);
                budget.setQnt(qnt);
                budget.setUntVal(untVal);
                budget.setStatus(status);
                budget.setActive(1);
                budget.setCreated(LocalDateTime.now());
                budget.setModified(LocalDateTime.now());
                budget.setSector(sector);
                budget.setBudgetType(budgetType);

                return budget;
            }

            private static Budget callUpdateBudget(Budget budget) throws SQLException, ClassNotFoundException {

                String name = JOptionPane.showInputDialog(null, "Informe o nome do orçamento: ", budget.getName());
                String item = JOptionPane.showInputDialog(null, "Informe o item do orçamento: ", budget.getItem());
                Integer qnt = Integer.parseInt(JOptionPane.showInputDialog(null, "Informe a quantidade de itens: ", budget.getQnt()));
                Double untVal = Double.parseDouble(JOptionPane.showInputDialog(null, "Informe o valor unitário do item: ", budget.getUntVal()));

                Object[] arrayStatus = BudgetStatus.getEnumArray();

                Object auxStatus = JOptionPane.showInputDialog(null, "Informe o status do orçamento: ", null, JOptionPane.QUESTION_MESSAGE, null, arrayStatus, arrayStatus[budget.getStatus()]);
                Integer status = BudgetStatus.getEnumIntValue(auxStatus);

                Object[] optionsActive = Active.getEnumArray();
                Object activeAux = JOptionPane.showInputDialog(null, "Informe se o orçamento ativo ", null, JOptionPane.QUESTION_MESSAGE, null, optionsActive, optionsActive[budget.getActive()]);
                Integer active = Active.getEnumIntValue(activeAux);

                Object[] sectors = getSectorDAO().searchAllOnlyWithName();

                Object selectionSetor = JOptionPane.showInputDialog(null, "Informe o setor do orçamento: ", "Cadastro de orçamento", JOptionPane.QUESTION_MESSAGE, null, sectors, sectors[0]);
                List<Sector> auxSectors = getSectorDAO().searchByName((String) selectionSetor);
                Sector sector = auxSectors.get(0);

                Object[] budgetTypes = getBudgetTypeDAO().searchAllOnlyWithName();

                Object selectionBudgetType = JOptionPane.showInputDialog(null, "Informe o tipo do orçamento: ", "Cadastro de orçamento", JOptionPane.QUESTION_MESSAGE, null, budgetTypes, budgetTypes[0]);
                List<BudgetType> auxBudgetType = getBudgetTypeDAO().searchByName((String) selectionBudgetType);
                BudgetType budgetType = auxBudgetType.get(0);

                budget.setName(name);
                budget.setItem(item);
                budget.setQnt(qnt);
                budget.setUntVal(untVal);
                budget.setStatus(status);
                budget.setActive(active);
                budget.setModified(LocalDateTime.now());
                budget.setSector(sector);
                budget.setBudgetType(budgetType);

                return budget;
            }

            private static Budget selectBudget() throws SQLException, ClassNotFoundException {
                Object[] selectionValues = getBudgetDAO().searchAllReturnArray();
                String initialSelection = (String) selectionValues[0];
                Object selection = JOptionPane.showInputDialog(null, "Selecione um Exercício: ",
                        null, JOptionPane.QUESTION_MESSAGE, null, selectionValues, initialSelection);
                List<Budget> budgets = getBudgetDAO().searchByName(selection.toString());
                return budgets.get(0);
            }

    private static void callMenuTypesBudgets() throws Exception {
        String[] optionsMenuTypesBudgets = {"Novo", "Editar", "Excluir", "Voltar"};
        int menuTypesBudgets = JOptionPane.showOptionDialog(null, "Selecione uma ação para Tipos Orçamentos: ",
                "Menu Tipos Orçamentos",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, optionsMenuTypesBudgets, optionsMenuTypesBudgets[0]);

        BudgetType budgetType;
        switch (menuTypesBudgets) {
            case 0: //novo
                budgetType = callCreateBudgeType();
                getBudgetTypeDAO().save(budgetType);
                callMenuTypesBudgets();
                break;
            case 1: //editar
                budgetType = selectBudgetType();
                budgetType = callUpdateBudgetType(budgetType);

                if (budgetType != null)
                    getBudgetTypeDAO().save(budgetType);

                callMenuTypesBudgets();
                break;
            case 2: //excluir
                budgetType = selectBudgetType();
                getBudgetTypeDAO().remove(budgetType);

                callMenuTypesBudgets();
                break;
            case 3:
               callMenuEntities();
               break;
        }

    }

    private static BudgetType callCreateBudgeType(){
        BudgetType budgetType = new BudgetType();

        String name = JOptionPane.showInputDialog(null, "Informe o nome do orçamento",
                "Cadastro orçamento", JOptionPane.QUESTION_MESSAGE);

        budgetType.setName(name);
        budgetType.setActive(1);
        budgetType.setCreated(LocalDateTime.now());
        budgetType.setModified(LocalDateTime.now());

        return budgetType;
    }

    private static BudgetType callUpdateBudgetType(BudgetType budgetType){
        String name = JOptionPane.showInputDialog(null, "Informe o nome do tipo de orçamento",
                budgetType.getName());

        Object[] optionsActive = Active.getEnumArray();
        Object activeAux = JOptionPane.showInputDialog(null, "Informe se o tipo de orçamento está ativo ", null, JOptionPane.QUESTION_MESSAGE, null, optionsActive, optionsActive[budgetType.getActive()]);
        Integer active = Active.getEnumIntValue(activeAux);

        budgetType.setName(name);
        budgetType.setActive(active);
        budgetType.setModified(LocalDateTime.now());

        return budgetType;
    }

    private static BudgetType selectBudgetType() throws SQLException, ClassNotFoundException {
        Object[] selectionValues = getBudgetTypeDAO().searchAllReturnArray();
        String initialSelection = (String) selectionValues[0];
        Object selection = JOptionPane.showInputDialog(null, "Selecione um tipo de orçamento",
                null, JOptionPane.QUESTION_MESSAGE, null, selectionValues, initialSelection);
        List<BudgetType> budgetTypes = getBudgetTypeDAO().searchByName((String) selection);
        return budgetTypes.get(0);
    }

    public static UserDAO getUserDAO() {
        UserDAO userDAO = new UserDAO();
        return userDAO;
    }

    public static SectorDAO getSectorDAO() {
        SectorDAO sectorDAO = new SectorDAO();
        return sectorDAO;
    }

    public static ExerciseDAO getExerciseDAO() {
        ExerciseDAO exerciseDAO = new ExerciseDAO();
        return exerciseDAO;
    }

    public static BudgetDAO getBudgetDAO() {
        BudgetDAO budgetDAO = new BudgetDAO();
        return budgetDAO;
    }

    public static BudgetTypeDAO getBudgetTypeDAO() {
        BudgetTypeDAO budgetTypeDAO = new BudgetTypeDAO();
        return budgetTypeDAO;
    }
}