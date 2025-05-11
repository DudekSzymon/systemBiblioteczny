import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class LibrarySystem {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new LoginApp();
        });
    }
}

class LoginApp extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private HashMap<String, HashMap<String, String>> accounts;

    public LoginApp() {
        setTitle("System Biblioteczny - Logowanie");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Konta testowe
        accounts = new HashMap<>();
        HashMap<String, String> adminAccount = new HashMap<>();
        adminAccount.put("password", "admin");
        adminAccount.put("role", "admin");
        adminAccount.put("name", "Administrator");
        accounts.put("admin", adminAccount);

        HashMap<String, String> userAccount = new HashMap<>();
        userAccount.put("password", "uzytkownik");
        userAccount.put("role", "user");
        userAccount.put("name", "Jan Kowalski");
        accounts.put("uzytkownik", userAccount);

        createLoginWidgets();
        setVisible(true);
    }

    private void createLoginWidgets() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("System Biblioteczny");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel usernameLabel = new JLabel("Nazwa użytkownika lub Email:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameField = new JTextField(20);
        usernameField.setMaximumSize(new Dimension(300, 25));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel passwordLabel = new JLabel("Hasło:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        passwordField = new JPasswordField(20);
        passwordField.setMaximumSize(new Dimension(300, 25));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    login();
                }
            }
        });

        JButton loginButton = new JButton("Zaloguj");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(e -> login());

        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(usernameLabel);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(usernameField);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(passwordLabel);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(passwordField);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(loginButton);

        add(mainPanel);
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (accounts.containsKey(username) && accounts.get(username).get("password").equals(password)) {
            String role = accounts.get(username).get("role");
            String userName = accounts.get(username).get("name");
            dispose();
            new BibliotekApp(role, userName, accounts);
        } else {
            JOptionPane.showMessageDialog(this, "Nieprawidłowa nazwa użytkownika lub hasło",
                    "Błąd logowania", JOptionPane.ERROR_MESSAGE);
        }
    }
}

class BibliotekApp extends JFrame {
    private String role;
    private String userName;
    private JTabbedPane notebook;
    private JTable bookTable, readersTable, currentBorrowsTable, historyBorrowsTable;
    private HashMap<String, HashMap<String, String>> accounts;
    private HashMap<Integer, ArrayList<HashMap<String, Object>>> userBorrowings;
    private HashMap<Integer, ArrayList<HashMap<String, Object>>> userBorrowingHistory;

    public BibliotekApp(String role, String userName, HashMap<String, HashMap<String, String>> accounts) {
        this.role = role;
        this.userName = userName;
        this.accounts = accounts;

        // Inicjalizacja danych wypożyczeń
        initializeBorrowingData();

        setTitle("System Biblioteczny - Politechnika Świętokrzyska");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Dodanie przycisku wylogowania w prawym górnym rogu
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutButton = new JButton("Wyloguj");
        logoutButton.addActionListener(e -> logout());
        logoutPanel.add(logoutButton);
        topPanel.add(logoutPanel, BorderLayout.NORTH);

        // Główny panel z zakładkami
        notebook = new JTabbedPane();
        topPanel.add(notebook, BorderLayout.CENTER);

        // Dodanie głównego panelu do ramki
        add(topPanel, BorderLayout.CENTER);

        // Tworzenie zakładek i pozostałych elementów
        createWidgets();
        createStatusBar();
        createFooter();

        setVisible(true);
    }

    private void initializeBorrowingData() {
        // Inicjalizacja przykładowych danych wypożyczeń
        userBorrowings = new HashMap<>();
        userBorrowingHistory = new HashMap<>();

        // Przykładowe aktualne wypożyczenia dla użytkownika o ID 1
        ArrayList<HashMap<String, Object>> borrowings = new ArrayList<>();
        HashMap<String, Object> borrowing1 = new HashMap<>();
        borrowing1.put("id", 1);
        borrowing1.put("title", "Harry Potter i Kamień Filozoficzny");
        borrowing1.put("author", "J.K. Rowling");
        borrowing1.put("borrowDate", "2025-04-25");
        borrowing1.put("returnDate", "2025-05-25");
        borrowing1.put("remainingDays", "22 dni");
        borrowings.add(borrowing1);

        HashMap<String, Object> borrowing2 = new HashMap<>();
        borrowing2.put("id", 2);
        borrowing2.put("title", "Władca Pierścieni: Dwie Wieże");
        borrowing2.put("author", "J.R.R. Tolkien");
        borrowing2.put("borrowDate", "2025-04-15");
        borrowing2.put("returnDate", "2025-05-15");
        borrowing2.put("remainingDays", "12 dni");
        borrowings.add(borrowing2);

        userBorrowings.put(1, borrowings);

        // Przykładowa historia wypożyczeń dla użytkownika o ID 1
        ArrayList<HashMap<String, Object>> history = new ArrayList<>();
        HashMap<String, Object> history1 = new HashMap<>();
        history1.put("id", 1);
        history1.put("title", "Harry Potter i Komnata Tajemnic");
        history1.put("author", "J.K. Rowling");
        history1.put("borrowDate", "2025-03-01");
        history1.put("returnDate", "2025-03-21");
        history1.put("status", "Zwrócona");
        history.add(history1);

        HashMap<String, Object> history2 = new HashMap<>();
        history2.put("id", 2);
        history2.put("title", "Duma i uprzedzenie");
        history2.put("author", "Jane Austen");
        history2.put("borrowDate", "2025-02-15");
        history2.put("returnDate", "2025-03-15");
        history2.put("status", "Zwrócona");
        history.add(history2);

        HashMap<String, Object> history3 = new HashMap<>();
        history3.put("id", 3);
        history3.put("title", "Zbrodnia i kara");
        history3.put("author", "Fiodor Dostojewski");
        history3.put("borrowDate", "2025-01-20");
        history3.put("returnDate", "2025-02-20");
        history3.put("status", "Zwrócona z opóźnieniem");
        history.add(history3);

        userBorrowingHistory.put(1, history);
    }

    private void createWidgets() {
        // Zakładka Książki
        JPanel ksiazkiPanel = new JPanel(new BorderLayout());
        notebook.addTab("Książki", ksiazkiPanel);
        createKsiazkiWidgets(ksiazkiPanel);

        // Dodawanie zakładek w zależności od roli
        if (role.equals("admin")) {
            JPanel czytelnicyPanel = new JPanel(new BorderLayout());
            notebook.addTab("Czytelnicy", czytelnicyPanel);
            createCzytelnicyWidgets(czytelnicyPanel);
        } else if (role.equals("user")) {
            JPanel mojeKontoPanel = new JPanel(new BorderLayout());
            notebook.addTab("Moje konto", mojeKontoPanel);
            createMojeKontoWidgets(mojeKontoPanel);
        }
    }

    private void createKsiazkiWidgets(JPanel panel) {
        // Nagłówek
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel headerLabel = new JLabel("Zarządzanie książkami");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(headerLabel);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Panel centralny
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Panel wyszukiwania
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Wyszukiwanie i filtrowanie"));
        searchPanel.add(new JLabel("Szukaj:"));
        JTextField searchField = new JTextField(30);
        searchPanel.add(searchField);
        JButton searchButton = new JButton("Szukaj");
        searchPanel.add(searchButton);
        centerPanel.add(searchPanel, BorderLayout.NORTH);

        // Panel przycisków
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Dodawanie przycisku tylko dla administratora
        if (role.equals("admin")) {
            JButton addBookButton = new JButton("Dodaj książkę");
            addBookButton.addActionListener(e -> showAddBookDialog());
            buttonPanel.add(addBookButton);

            JButton deleteBookButton = new JButton("Usuń książkę");
            deleteBookButton.addActionListener(e -> deleteSelectedBook());
            buttonPanel.add(deleteBookButton);
        }

        centerPanel.add(buttonPanel, BorderLayout.CENTER);

        // Tabela książek
        String[] columnNames = {"ID", "Tytuł", "Autor", "Ilość", "Stan"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Dodanie przykładowych danych
        model.addRow(new Object[]{1, "Harry Potter i Kamień Filozoficzny", "J.K. Rowling", 5, "Nowy"});
        model.addRow(new Object[]{2, "Władca Pierścieni", "J.R.R. Tolkien", 3, "Uszkodzony"});
        model.addRow(new Object[]{3, "Zbrodnia i kara", "Fiodor Dostojewski", 2, "Zniszczony"});

        bookTable = new JTable(model);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookTable.getTableHeader().setReorderingAllowed(false);

        // Ustawienie szerokości kolumn
        bookTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        bookTable.getColumnModel().getColumn(1).setPreferredWidth(300);
        bookTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        bookTable.getColumnModel().getColumn(3).setPreferredWidth(50);
        bookTable.getColumnModel().getColumn(4).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(bookTable);
        centerPanel.add(scrollPane, BorderLayout.SOUTH);

        panel.add(centerPanel, BorderLayout.CENTER);
    }

    private void createCzytelnicyWidgets(JPanel panel) {
        // Nagłówek
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel headerLabel = new JLabel("Zarządzanie czytelnikami");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(headerLabel);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Panel centralny
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Panel wyszukiwania
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Wyszukiwanie czytelników"));
        searchPanel.add(new JLabel("Szukaj:"));
        JTextField searchField = new JTextField(30);
        searchPanel.add(searchField);
        JButton searchButton = new JButton("Szukaj");
        searchPanel.add(searchButton);
        centerPanel.add(searchPanel, BorderLayout.NORTH);

        // Panel przycisków
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addReaderButton = new JButton("Dodaj czytelnika");
        addReaderButton.addActionListener(e -> showAddUserDialog());
        buttonPanel.add(addReaderButton);

        JButton deleteReaderButton = new JButton("Usuń czytelnika");
        deleteReaderButton.addActionListener(e -> deleteSelectedReader());
        buttonPanel.add(deleteReaderButton);

        JButton borrowButton = new JButton("Wypożycz książkę");
        borrowButton.addActionListener(e -> showBorrowBookDialog());
        buttonPanel.add(borrowButton);

        centerPanel.add(buttonPanel, BorderLayout.CENTER);

        // Tabela czytelników
        String[] columnNames = {"ID", "Imię", "Nazwisko", "PESEL", "Adres", "Email", "Telefon", "Data urodzenia"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Dodanie przykładowych danych
        model.addRow(new Object[]{
                1, "Jan", "Kowalski", "12345678910", "Al. Tysiąclecia Państwa Polskiego 7, 25-314 Kielce",
                "Jan.Kowalski@gmail.com", "123-456-789", "1999-11-11"
        });

        readersTable = new JTable(model);
        readersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        readersTable.getTableHeader().setReorderingAllowed(false);

        // Dodanie listenera do tabeli czytelników
        readersTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = readersTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        showReaderBorrowingsDialog(selectedRow);
                    }
                }
            }
        });

        // Ustawienie szerokości kolumn
        readersTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        readersTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        readersTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        readersTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        readersTable.getColumnModel().getColumn(4).setPreferredWidth(250);
        readersTable.getColumnModel().getColumn(5).setPreferredWidth(180);
        readersTable.getColumnModel().getColumn(6).setPreferredWidth(120);
        readersTable.getColumnModel().getColumn(7).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(readersTable);
        centerPanel.add(scrollPane, BorderLayout.SOUTH);

        panel.add(centerPanel, BorderLayout.CENTER);
    }

    private void createMojeKontoWidgets(JPanel panel) {
        // Nagłówek
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel headerLabel = new JLabel("Witaj, " + userName + "!");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(headerLabel);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Panel centralny z zakładkami
        JTabbedPane userNotebook = new JTabbedPane();

        // Zakładka aktualnie wypożyczone
        JPanel currentPanel = new JPanel(new BorderLayout());
        userNotebook.addTab("Aktualnie wypożyczone", currentPanel);

        // Zakładka historia wypożyczeń
        JPanel historyPanel = new JPanel(new BorderLayout());
        userNotebook.addTab("Historia wypożyczeń", historyPanel);

        // Tabela aktualnie wypożyczonych
        String[] columnNames = {"ID", "Tytuł", "Autor", "Data wypożyczenia", "Termin zwrotu", "Pozostały czas"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Pobierz wypożyczenia dla użytkownika o ID 1 (przykładowe)
        ArrayList<HashMap<String, Object>> borrowings = userBorrowings.get(1);
        if (borrowings != null) {
            for (HashMap<String, Object> borrowing : borrowings) {
                model.addRow(new Object[]{
                        borrowing.get("id"),
                        borrowing.get("title"),
                        borrowing.get("author"),
                        borrowing.get("borrowDate"),
                        borrowing.get("returnDate"),
                        borrowing.get("remainingDays")
                });
            }
        }

        currentBorrowsTable = new JTable(model);
        currentBorrowsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        currentBorrowsTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(currentBorrowsTable);
        currentPanel.add(scrollPane, BorderLayout.CENTER);

        // Tabela historii wypożyczeń
        String[] historyColumns = {"ID", "Tytuł", "Autor", "Data wypożyczenia", "Data zwrotu", "Status"};
        DefaultTableModel historyModel = new DefaultTableModel(historyColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Pobierz historię wypożyczeń dla użytkownika o ID 1 (przykładowe)
        ArrayList<HashMap<String, Object>> history = userBorrowingHistory.get(1);
        if (history != null) {
            for (HashMap<String, Object> historyItem : history) {
                historyModel.addRow(new Object[]{
                        historyItem.get("id"),
                        historyItem.get("title"),
                        historyItem.get("author"),
                        historyItem.get("borrowDate"),
                        historyItem.get("returnDate"),
                        historyItem.get("status")
                });
            }
        }

        historyBorrowsTable = new JTable(historyModel);
        historyBorrowsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyBorrowsTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane historyScrollPane = new JScrollPane(historyBorrowsTable);
        historyPanel.add(historyScrollPane, BorderLayout.CENTER);

        panel.add(userNotebook, BorderLayout.CENTER);
    }

    private void createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder());

        JLabel statusLabel = new JLabel("Zalogowano jako: " + userName + " (" + role + ")");
        statusBar.add(statusLabel, BorderLayout.WEST);

        add(statusBar, BorderLayout.SOUTH);
    }

    private void createFooter() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel footerLabel = new JLabel("© 2025 System Biblioteczny - Politechnika Świętokrzyska");
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        footerPanel.add(footerLabel);

        add(footerPanel, BorderLayout.PAGE_END);
    }

    private void showReaderBorrowingsDialog(int readerRow) {
        int readerId = (int) readersTable.getValueAt(readerRow, 0);
        String readerName = readersTable.getValueAt(readerRow, 1) + " " + readersTable.getValueAt(readerRow, 2);

        JDialog dialog = new JDialog(this, "Wypożyczenia czytelnika: " + readerName, false);
        dialog.setSize(800, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        // Panel aktualnych wypożyczeń
        JPanel currentPanel = new JPanel(new BorderLayout());
        String[] currentColumns = {"ID", "Tytuł", "Autor", "Data wypożyczenia", "Termin zwrotu", "Pozostały czas"};
        DefaultTableModel currentModel = new DefaultTableModel(currentColumns, 0);

        // Pobierz wypożyczenia dla wybranego użytkownika
        ArrayList<HashMap<String, Object>> borrowings = userBorrowings.get(readerId);
        if (borrowings != null) {
            for (HashMap<String, Object> borrowing : borrowings) {
                currentModel.addRow(new Object[]{
                        borrowing.get("id"),
                        borrowing.get("title"),
                        borrowing.get("author"),
                        borrowing.get("borrowDate"),
                        borrowing.get("returnDate"),
                        borrowing.get("remainingDays")
                });
            }
        }

        JTable currentTable = new JTable(currentModel);
        JScrollPane currentScrollPane = new JScrollPane(currentTable);
        currentPanel.add(currentScrollPane, BorderLayout.CENTER);

        // Panel historii wypożyczeń
        JPanel historyPanel = new JPanel(new BorderLayout());
        String[] historyColumns = {"ID", "Tytuł", "Autor", "Data wypożyczenia", "Data zwrotu", "Status"};
        DefaultTableModel historyModel = new DefaultTableModel(historyColumns, 0);

        // Pobierz historię wypożyczeń dla wybranego użytkownika
        ArrayList<HashMap<String, Object>> history = userBorrowingHistory.get(readerId);
        if (history != null) {
            for (HashMap<String, Object> historyItem : history) {
                historyModel.addRow(new Object[]{
                        historyItem.get("id"),
                        historyItem.get("title"),
                        historyItem.get("author"),
                        historyItem.get("borrowDate"),
                        historyItem.get("returnDate"),
                        historyItem.get("status")
                });
            }
        }

        JTable historyTable = new JTable(historyModel);
        JScrollPane historyScrollPane = new JScrollPane(historyTable);
        historyPanel.add(historyScrollPane, BorderLayout.CENTER);

        // Dodaj panele do zakładek
        tabbedPane.addTab("Aktualne wypożyczenia", currentPanel);
        tabbedPane.addTab("Historia wypożyczeń", historyPanel);

        dialog.add(tabbedPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("Zamknij");
        closeButton.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void showAddUserDialog() {
        JDialog dialog = new JDialog(this, "Dodaj czytelnika", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(9, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("Imię:"));
        JTextField firstNameField = new JTextField();
        formPanel.add(firstNameField);

        formPanel.add(new JLabel("Nazwisko:"));
        JTextField lastNameField = new JTextField();
        formPanel.add(lastNameField);

        formPanel.add(new JLabel("PESEL:"));
        JTextField peselField = new JTextField();
        formPanel.add(peselField);

        formPanel.add(new JLabel("Adres:"));
        JTextField addressField = new JTextField();
        formPanel.add(addressField);

        formPanel.add(new JLabel("Email:"));
        JTextField emailField = new JTextField();
        formPanel.add(emailField);

        formPanel.add(new JLabel("Telefon:"));
        JTextField phoneField = new JTextField();
        formPanel.add(phoneField);

        formPanel.add(new JLabel("Data urodzenia (YYYY-MM-DD):"));
        JTextField birthDateField = new JTextField();
        formPanel.add(birthDateField);

        formPanel.add(new JLabel("Nazwa użytkownika:"));
        JTextField usernameField = new JTextField();
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Hasło:"));
        JPasswordField passwordField = new JPasswordField();
        formPanel.add(passwordField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Anuluj");
        cancelButton.addActionListener(e -> dialog.dispose());

        JButton saveButton = new JButton("Zapisz");
        saveButton.addActionListener(e -> {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String pesel = peselField.getText();
            String address = addressField.getText();
            String email = emailField.getText();
            String phone = phoneField.getText();
            String birthDate = birthDateField.getText();
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Wypełnij wszystkie wymagane pola",
                        "Błąd", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Dodaj użytkownika do systemu
            addUser(firstName, lastName, pesel, address, email, phone, birthDate, username, password);
            dialog.dispose();
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void addUser(String firstName, String lastName, String pesel, String address,
                         String email, String phone, String birthDate, String username, String password) {
        // Dodaj użytkownika do kont
        HashMap<String, String> userAccount = new HashMap<>();
        userAccount.put("password", password);
        userAccount.put("role", "user");
        userAccount.put("name", firstName + " " + lastName);
        accounts.put(username, userAccount);

        // Dodaj użytkownika do tabeli czytelników
        DefaultTableModel model = (DefaultTableModel) readersTable.getModel();
        int id = model.getRowCount() + 1;
        model.addRow(new Object[]{
                id, firstName, lastName, pesel, address, email, phone, birthDate
        });

        // Powiadom tabelę o zmianach
        model.fireTableDataChanged();

        // Inicjalizuj puste listy wypożyczeń dla nowego użytkownika
        userBorrowings.put(id, new ArrayList<>());
        userBorrowingHistory.put(id, new ArrayList<>());

        JOptionPane.showMessageDialog(this, "Czytelnik został dodany pomyślnie",
                "Sukces", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteSelectedReader() {
        int selectedRow = readersTable.getSelectedRow();
        if (selectedRow >= 0) {
            DefaultTableModel model = (DefaultTableModel) readersTable.getModel();
            model.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this, "Czytelnik został usunięty",
                    "Informacja", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Wybierz czytelnika do usunięcia",
                    "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddBookDialog() {
        JDialog dialog = new JDialog(this, "Dodaj książkę", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("Tytuł:"));
        JTextField titleField = new JTextField();
        formPanel.add(titleField);

        formPanel.add(new JLabel("Autor:"));
        JTextField authorField = new JTextField();
        formPanel.add(authorField);

        formPanel.add(new JLabel("Ilość:"));
        JTextField quantityField = new JTextField();
        formPanel.add(quantityField);

        formPanel.add(new JLabel("Stan:"));
        String[] states = {"Nowy", "Używany", "Uszkodzony", "Zniszczony"};
        JComboBox<String> stateComboBox = new JComboBox<>(states);
        formPanel.add(stateComboBox);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Anuluj");
        cancelButton.addActionListener(e -> dialog.dispose());

        JButton saveButton = new JButton("Zapisz");
        saveButton.addActionListener(e -> {
            String title = titleField.getText();
            String author = authorField.getText();
            String quantityText = quantityField.getText();
            String state = (String) stateComboBox.getSelectedItem();

            if (title.isEmpty() || author.isEmpty() || quantityText.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Wypełnij wszystkie wymagane pola",
                        "Błąd", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int quantity = Integer.parseInt(quantityText);
                addBook(title, author, quantity, state);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Ilość musi być liczbą",
                        "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void addBook(String title, String author, int quantity, String state) {
        DefaultTableModel model = (DefaultTableModel) bookTable.getModel();
        int id = model.getRowCount() + 1;
        model.addRow(new Object[]{id, title, author, quantity, state});

        // Powiadom tabelę o zmianach
        model.fireTableDataChanged();

        JOptionPane.showMessageDialog(this, "Książka została dodana pomyślnie",
                "Sukces", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteSelectedBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow >= 0) {
            DefaultTableModel model = (DefaultTableModel) bookTable.getModel();
            model.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this, "Książka została usunięta",
                    "Informacja", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Wybierz książkę do usunięcia",
                    "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showBorrowBookDialog() {
        JDialog dialog = new JDialog(this, "Wypożycz książkę", true);
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Wybór czytelnika
        formPanel.add(new JLabel("Wybierz czytelnika:"));
        DefaultTableModel readerModel = (DefaultTableModel) readersTable.getModel();
        Vector<String> readers = new Vector<>();
        for (int i = 0; i < readerModel.getRowCount(); i++) {
            readers.add(readerModel.getValueAt(i, 1) + " " + readerModel.getValueAt(i, 2));
        }
        JComboBox<String> readerComboBox = new JComboBox<>(readers);
        formPanel.add(readerComboBox);

        // Wybór książki
        formPanel.add(new JLabel("Wybierz książkę:"));
        DefaultTableModel bookModel = (DefaultTableModel) bookTable.getModel();
        Vector<String> books = new Vector<>();
        for (int i = 0; i < bookModel.getRowCount(); i++) {
            books.add(bookModel.getValueAt(i, 1) + " - " + bookModel.getValueAt(i, 2));
        }
        JComboBox<String> bookComboBox = new JComboBox<>(books);
        formPanel.add(bookComboBox);

        // Data wypożyczenia
        formPanel.add(new JLabel("Data wypożyczenia:"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        JTextField borrowDateField = new JTextField(dateFormat.format(new Date()));
        formPanel.add(borrowDateField);

        // Data zwrotu
        formPanel.add(new JLabel("Data zwrotu:"));
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);
        JTextField returnDateField = new JTextField(dateFormat.format(cal.getTime()));
        formPanel.add(returnDateField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Anuluj");
        cancelButton.addActionListener(e -> dialog.dispose());

        JButton saveButton = new JButton("Wypożycz");
        saveButton.addActionListener(e -> {
            int readerIndex = readerComboBox.getSelectedIndex();
            int bookIndex = bookComboBox.getSelectedIndex();
            String borrowDate = borrowDateField.getText();
            String returnDate = returnDateField.getText();

            if (readerIndex >= 0 && bookIndex >= 0) {
                createBorrow(readerIndex, bookIndex, borrowDate, returnDate);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Wybierz czytelnika i książkę",
                        "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void createBorrow(int readerIndex, int bookIndex, String borrowDate, String returnDate) {
        // Pobierz dane czytelnika i książki
        DefaultTableModel readerModel = (DefaultTableModel) readersTable.getModel();
        int readerId = (int) readerModel.getValueAt(readerIndex, 0);
        String readerName = readerModel.getValueAt(readerIndex, 1) + " " + readerModel.getValueAt(readerIndex, 2);

        DefaultTableModel bookModel = (DefaultTableModel) bookTable.getModel();
        int bookId = (int) bookModel.getValueAt(bookIndex, 0);
        String bookTitle = (String) bookModel.getValueAt(bookIndex, 1);
        String bookAuthor = (String) bookModel.getValueAt(bookIndex, 2);

        // Zmniejsz dostępną ilość książek
        int currentQuantity = (int) bookModel.getValueAt(bookIndex, 3);
        if (currentQuantity > 0) {
            bookModel.setValueAt(currentQuantity - 1, bookIndex, 3);

            // Oblicz pozostały czas
            String remainingDays = "30 dni";

            // Dodaj wypożyczenie do listy wypożyczeń użytkownika
            ArrayList<HashMap<String, Object>> borrowings = userBorrowings.get(readerId);
            if (borrowings == null) {
                borrowings = new ArrayList<>();
                userBorrowings.put(readerId, borrowings);
            }

            HashMap<String, Object> newBorrowing = new HashMap<>();
            newBorrowing.put("id", bookId);
            newBorrowing.put("title", bookTitle);
            newBorrowing.put("author", bookAuthor);
            newBorrowing.put("borrowDate", borrowDate);
            newBorrowing.put("returnDate", returnDate);
            newBorrowing.put("remainingDays", remainingDays);
            borrowings.add(newBorrowing);

            // Dodaj informację o wypożyczeniu
            JOptionPane.showMessageDialog(this,
                    "Książka \"" + bookTitle + "\" została wypożyczona dla " + readerName + "\n" +
                            "Data wypożyczenia: " + borrowDate + "\n" +
                            "Termin zwrotu: " + returnDate,
                    "Wypożyczenie", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Brak dostępnych egzemplarzy książki",
                    "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logout() {
        dispose();
        new LoginApp();
    }
}
