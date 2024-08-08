package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.*;
import dic_exec.*;
import javazoom.jl.player.Player;
import org.jfree.chart.*;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

public class gui extends JFrame {
    private JPanel panel1, panel2, panel1_0, panel1_1, panel1_2, panel1_3;
    private JTextField kotoba_line, count_lcd, book_lcd;
    private JButton sgs_btn, mp3_btn, add_book_btn, add_btn, modify_btn, del_btn, reset_btn, del_book_btn;
    private JList<String> list, bookmark_view;
    private JTextArea imi_out, history_view;
    private JTabbedPane tabbedPane1;
    private dic_exec dic;
    private historia history;
    private Vector<tuple> slist, book;
    private DefaultListModel<String> koumoku, blist;
    private String ima_kotoba = "";
    private List<Integer> count;
    private int book_count;
    private Vector<String> history_list;
    private DefaultPieDataset dataset;
    private JFreeChart chart;
    private PiePlot plot;
    private ChartPanel chartPanel;
    public gui() {
        create_db();
        initComponents();
    }
    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("ポリゴン切れ");
        ImageIcon icon1 = new ImageIcon(getClass().getResource("/search.png"));
        ImageIcon icon2 = new ImageIcon(getClass().getResource("/mp3.png"));
        ImageIcon icon3 = new ImageIcon(getClass().getResource("/bookmarkadd.png"));
        ImageIcon icon4 = new ImageIcon(getClass().getResource("/add.png"));
        ImageIcon icon5 = new ImageIcon(getClass().getResource("/del.png"));
        ImageIcon icon6 = new ImageIcon(getClass().getResource("/reset.png"));
        ImageIcon icon7 = new ImageIcon(getClass().getResource("/modify.png"));
        count_();
        panel1 = new JPanel(new BorderLayout());
        tabbedPane1 = new JTabbedPane();
        panel1_0 = new JPanel(new BorderLayout());
        JPanel panel1_top = new JPanel(new GridBagLayout());
        kotoba_line = new JTextField(20);
        Dimension kls = kotoba_line.getPreferredSize();
        kls.height = 34;
        kotoba_line.setPreferredSize(kls);
        sgs_btn = new JButton(icon1);
        sgs_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sgs();
            }
        });
        koumoku = new DefaultListModel<>();
        list = new JList<>(koumoku);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int i = list.getSelectedIndex();
                if (i == -1){
                    return;
                }
                set_imi_out(i);
            }
        });
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int i = list.locationToIndex(e.getPoint());
                if (i == -1){
                    return;
                }
                set_imi_out(i);
            }
        });
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel1_top.add(new JLabel("検索 : "), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1_top.add(kotoba_line, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        panel1_top.add(sgs_btn, gbc);
        panel1_0.add(panel1_top, BorderLayout.NORTH);
        panel1_0.add(new JScrollPane(list), BorderLayout.CENTER);
        panel1_1 = new JPanel(new BorderLayout());
        panel1_2 = new JPanel(new BorderLayout());
        panel1_3 = new JPanel(new BorderLayout());
        JPanel book_bar = new JPanel(new GridBagLayout());
        GridBagConstraints gbc_0 = new GridBagConstraints();
        gbc_0.insets = new Insets(5, 5, 5, 5);
        gbc_0.gridx = 0;
        gbc_0.gridy = 0;
        gbc_0.weightx = 1.0;
        del_book_btn = new JButton(icon5);
        del_book_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                del_book();
            }
        });
        book_bar.add(del_book_btn, gbc_0);
        gbc_0.gridx = 1;
        gbc_0.gridy = 0;
        gbc_0.weightx = 1.0;
        book_bar.add(new JPanel(), gbc_0);
        book_lcd = new JTextField(20);
        book_lcd.setPreferredSize(kls);
        book_lcd.setEditable(false);
        gbc_0.gridx = 3;
        gbc_0.gridy = 0;
        gbc_0.weightx = 3.0;
        gbc_0.fill = GridBagConstraints.HORIZONTAL;
        book_bar.add(book_lcd, gbc_0);
        panel1_1.add(book_bar, BorderLayout.NORTH);
        blist = new DefaultListModel<>();
        load_book();
        book_lcd.setText(String.valueOf(book_count));
        bookmark_view = new JList<>(blist);
        bookmark_view.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel1_1.add(new JScrollPane(bookmark_view), BorderLayout.CENTER);
        bookmark_view.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                String t = bookmark_view.getSelectedValue();
                if (t == null){
                    return;
                }
                String[] t_ = t.split("  ");
                set_imi_out(t_[t_.length-1]);
            }
        });
        bookmark_view.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String t = bookmark_view.getSelectedValue();
                if (t == null){
                    return;
                }
                String[] t_ = t.split("  ");
                set_imi_out(t_[t_.length-1]);
            }
        });
        history_view = new JTextArea();
        history_view.setEditable(false);
        history_load();
        JScrollPane scrollPane = new JScrollPane(history_view);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel1_2.add(scrollPane, BorderLayout.CENTER);
        GridBagConstraints gbc_1 = new GridBagConstraints();
        JPanel history_bar = new JPanel(new GridBagLayout());
        gbc_1.insets = new Insets(5, 5, 5, 5);
        gbc_1.gridx = 0;
        gbc_1.gridy = 0;
        gbc_1.weightx = 1.0;
        history_bar.add(new JPanel(), gbc_1);
        gbc_1.gridx = 1;
        gbc_1.gridy = 0;
        gbc_1.anchor = GridBagConstraints.EAST;
        gbc_1.fill = GridBagConstraints.NONE;
        reset_btn = new JButton(icon6);
        reset_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final String F = "history.txt";
                File file = new File(F);
                if (file.exists()) {
                    file.delete();
                } else {
                    return;
                }
                history_load();
            }
        });
        history_bar.add(reset_btn, gbc_1);
        panel1_2.add(history_bar, BorderLayout.SOUTH);
        JPanel count_view = new JPanel(new GridBagLayout());
        GridBagConstraints gbc_2 = new GridBagConstraints();
        gbc_2.insets = new Insets(5, 5, 5, 5);
        gbc_2.gridx = 0;
        gbc_2.gridy = 0;
        count_view.add(new JLabel("登録された見出し語の数 : "), gbc_2);
        count_lcd = new JTextField(20);
        count_lcd.setPreferredSize(kls);
        count_lcd.setText(count.get(0).toString());
        count_lcd.setEditable(false);
        gbc_2.gridx = 1;
        gbc_2.weightx = 1.0;
        gbc_2.fill = GridBagConstraints.HORIZONTAL;
        count_view.add(count_lcd, gbc_2);
        panel1_3.add(count_view, BorderLayout.NORTH);
        chart_();
        tabbedPane1.addTab("検索", panel1_0);
        tabbedPane1.addTab("ブックマーク", panel1_1);
        tabbedPane1.addTab("検索記録", panel1_2);
        tabbedPane1.addTab("統計", panel1_3);
        tabbedPane1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int i = tabbedPane1.getSelectedIndex();
                switch (i){
                    case 0:
                        mp3_btn.setEnabled(true);
                        add_book_btn.setEnabled(true);
                        add_btn.setEnabled(true);
                        modify_btn.setEnabled(true);
                        del_btn.setEnabled(true);
                        imi_out.setText("");
                        int n = list.getSelectedIndex();
                        if (n == -1){
                            return;
                        }
                        set_imi_out(n);
                        break;
                    case 1:
                        mp3_btn.setEnabled(true);
                        add_book_btn.setEnabled(false);
                        add_btn.setEnabled(false);
                        modify_btn.setEnabled(false);
                        del_btn.setEnabled(false);
                        imi_out.setText("");
                        String t = bookmark_view.getSelectedValue();
                        if (t == null){
                            return;
                        }
                        String[] t_ = t.split("  ");
                        set_imi_out(t_[t_.length-1]);
                        break;
                    default:
                        mp3_btn.setEnabled(false);
                        add_book_btn.setEnabled(false);
                        add_btn.setEnabled(false);
                        modify_btn.setEnabled(false);
                        del_btn.setEnabled(false);
                        imi_out.setText("");
                }
            }
        });
        panel1.add(tabbedPane1, BorderLayout.CENTER);
        panel2 = new JPanel(new BorderLayout());
        imi_out = new JTextArea();
        imi_out.setEditable(false);
        imi_out.setLineWrap(true);
        JPanel panel2_bottom = new JPanel(new GridLayout(1, 4));
        mp3_btn = new JButton(icon2);
        mp3_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                play_mp3();
            }
        });
        add_book_btn = new JButton(icon3);
        add_book_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                add_book();
                load_book();
                book_lcd.setText(String.valueOf(book_count));
            }
        });
        add_btn = new JButton(icon4);
        add_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean[] flag = new boolean[] { false };
                add_ui add_ui_ = new add_ui(flag,gui.this);
                add_ui_.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
                add_ui_.setVisible(true);
                count_();
                count_lcd.setText(count.get(0).toString());
                chart_();
                if(flag[0]){
                    sgs();
                }
            }
        });
        modify_btn = new JButton(icon7);
        modify_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i = list.getSelectedIndex();
                if (i == -1){
                    return;
                }
                boolean[] flag = new boolean[] { false };
                tuple b = new tuple();
                modify_ui modify_ui_ = new modify_ui(slist.get(i), flag, b, gui.this);
                modify_ui_.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
                modify_ui_.setVisible(true);
                count_();
                count_lcd.setText(count.get(0).toString());
                chart_();
                dic = new dic_exec();
                if(dic.del_book(slist.get(i).kotoba)){
                    dic.add_book(b);
                    load_book();
                }
                if (flag[0]){
                    sgs();
                }

            }
        });
        del_btn = new JButton(icon5);
        del_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                del_kotoba();
            }
        });
        panel2_bottom.add(mp3_btn);
        panel2_bottom.add(add_book_btn);
        panel2_bottom.add(add_btn);
        panel2_bottom.add(modify_btn);
        panel2_bottom.add(del_btn);
        panel2.add(new JScrollPane(imi_out), BorderLayout.CENTER);
        panel2.add(panel2_bottom, BorderLayout.SOUTH);
        setLayout(new GridLayout(1, 2));
        add(panel1);
        add(panel2);
        setSize(820, 600);
        setLocationRelativeTo(null);
        setVisible(true);
        sgs();
    }
    void sgs(){
        koumoku.clear();
        dic = new dic_exec();
        slist = new Vector<tuple>();
        String kotoba = kotoba_line.getText();
        slist = dic.sagasu(kotoba);
        int size = slist.size();
        String ktb, kj, text;
        for (int i = 0; i < size; i++) {
            ktb = slist.get(i).kotoba;
            kj = slist.get(i).kanji;
            if (ktb.compareTo(kj) == 0){
                text = ktb;
            } else {
                text = kj + "  " + ktb;
            }
            koumoku.addElement(text);
        }
        imi_out.setText("");
    }
    void set_imi_out(int index){
        tuple t = slist.get(index);
        String kotoba;
        String hinsi;
        String naiyou;
        if (t.kotoba.compareTo(t.kanji) == 0){
            kotoba = t.kotoba;
        } else {
            kotoba = t.kanji + "  " + t.kotoba;
        }
        switch (t.hinsi){
            case 0:
                hinsi = "[助詞]";
                break;
            case 1:
                hinsi = "[名詞]";
                break;
            case 2:
                hinsi = "[動詞]";
                break;
            case 3:
                hinsi = "[形容詞]";
                break;
            case 4:
                hinsi = "[副詞]";
                break;
            case 5:
                hinsi = "[その外]";
                break;
            default:
                hinsi = "[NULL]";
        }
        naiyou = kotoba + "\n" + hinsi + "\n" + t.imi + "\n" + t.bikou;
        imi_out.setText(naiyou);
        ima_kotoba = t.kotoba;
        historia his = new historia(t.kotoba, t.imi);
        history_load();
    }
    void load_book(){
        blist.clear();
        dic = new dic_exec();
        book = new Vector<tuple>(dic.load_book());
        book_count = dic.count_book();
        int size = book.size();
        String ktb, kj, text;
        for (int i = 0; i < size; i++) {
            ktb = book.get(i).kotoba;
            kj = book.get(i).kanji;
            if (ktb.compareTo(kj) == 0){
                text = ktb;
            } else {
                text = kj + "  " + ktb;
            }
            blist.addElement(text);
        }
    }
    void add_book() {
        if (list.getSelectedIndex() == -1){
            return;
        }
        tuple t = slist.get(list.getSelectedIndex());
        dic = new dic_exec();
        boolean f = dic.add_book(t);
        if (!f){
            JOptionPane.showMessageDialog(null, "すでに存在する言葉です。", "お知らせ", JOptionPane.WARNING_MESSAGE);
        }
        load_book();
    }
    void del_book(){
        String t = bookmark_view.getSelectedValue();
        if (t == null){
            return;
        }
        String[] t_ = t.split("  ");
        dic = new dic_exec();
        dic.del_book(t_[t_.length-1]);
        load_book();
        book_lcd.setText(String.valueOf(book_count));
        imi_out.setText("");
    }
    void set_imi_out(String kotoba_){
        dic = new dic_exec();
        tuple t = dic.sel_book(kotoba_);
        String kotoba;
        String hinsi;
        String naiyou;
        if (t.kotoba.compareTo(t.kanji) == 0){
            kotoba = t.kotoba;
        } else {
            kotoba = t.kanji + "  " + t.kotoba;
        }
        switch (t.hinsi){
            case 0:
                hinsi = "[助詞]";
                break;
            case 1:
                hinsi = "[名詞]";
                break;
            case 2:
                hinsi = "[動詞]";
                break;
            case 3:
                hinsi = "[形容詞]";
                break;
            case 4:
                hinsi = "[副詞]";
                break;
            case 5:
                hinsi = "[その外]";
                break;
            default:
                hinsi = "[NULL]";
        }
        naiyou = kotoba + "\n" + hinsi + "\n" + t.imi + "\n" + t.bikou;
        imi_out.setText(naiyou);
        ima_kotoba = t.kotoba;
    }
    void play_mp3(){
        if (ima_kotoba.compareTo("") == 0) {
            return;
        }
        dic = new dic_exec();
        byte[] tmp = dic.mp3_load(ima_kotoba);
        if (tmp.length < 4){
            return;
        }
        try {
            InputStream mp3 = new ByteArrayInputStream(tmp);
            Player player = new Player(mp3);
            player.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void count_(){
        dic = new dic_exec();
        count = dic.count_kotoba();
    }
    void history_load(){
        history = new historia();
        history_list = history.return_history();
        history_view.setText("");
        for (int i = 0; i < history_list.size(); i++){
            history_view.append(history_list.get(i) + '\n');
        }
    }
    void del_kotoba(){
        int i = list.getSelectedIndex();
        if (i == -1){
            return;
        }
        int t = JOptionPane.showConfirmDialog(this, "本当に消してもいいですか？", "警告", JOptionPane.YES_NO_OPTION);
        if (t == JOptionPane.YES_OPTION) {
            dic = new dic_exec();
            dic.del_kotoba(slist.get(i).kotoba);
            if (dic.del_book(slist.get(i).kotoba)){
                load_book();
                book_lcd.setText(String.valueOf(book_count));
            }
            sgs();
            imi_out.setText("");
            count_();
            count_lcd.setText(count.get(0).toString());
            chart_();
        }
    }
    void create_db(){
        File file = new File("dic.db");
        if (!file.exists()) {
            dic = new dic_exec();
            dic.create_dic();
        }
    }
    void chart_(){
        if (chartPanel != null) {
            panel1_3.remove(chartPanel);
        }
        dataset = new DefaultPieDataset();
        dataset.setValue("助詞", count.get(1));
        dataset.setValue("名詞", count.get(2));
        dataset.setValue("動詞", count.get(3));
        dataset.setValue("形容詞", count.get(4));
        dataset.setValue("副詞", count.get(5));
        dataset.setValue("その他", count.get(6));
        chart = ChartFactory.createPieChart("", dataset, false, true, false);
        plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("助詞", Color.getHSBColor(0.1667f, 0.5f, 0.85f));
        plot.setSectionPaint("名詞", Color.getHSBColor(0.9167f, 0.4f, 0.85f));
        plot.setSectionPaint("動詞", Color.getHSBColor(0.5556f, 0.4f, 0.85f));
        plot.setSectionPaint("形容詞", Color.getHSBColor(0.3333f, 0.5f, 0.85f));
        plot.setSectionPaint("副詞", Color.getHSBColor(0.75f, 0.25f, 0.90f));
        plot.setSectionPaint("その他", Color.LIGHT_GRAY);
        plot.setLabelFont(new Font("Noto Sans CJK JP", Font.PLAIN, 12));
        chartPanel = new ChartPanel(chart);
        panel1_3.add(chartPanel, BorderLayout.CENTER);
        panel1_3.revalidate();
        panel1_3.repaint();
    }
}

class add_ui extends JDialog {
    private JComboBox<String> comboBox;
    JTextField new_kotoba_line, new_kanji_line, new_mp3_line;
    JButton add_btn, attach_btn;
    JTextArea new_naiyou_line, new_bikou_line;
    private dic_exec dic;
    File file = null;
    boolean[] flag;
    public add_ui(boolean[] flag, JFrame parentFrame) {
        super(parentFrame, "見出し語追加", true);
        setSize(500, 400);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        comboBox = new JComboBox<>(new String[]{"助詞", "名詞", "動詞", "形容詞", "副詞", "その外"});
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(comboBox, gbc);
        new_kotoba_line = new JTextField(15);
        Dimension nkls = new_kotoba_line.getPreferredSize();
        nkls.height = 28;
        new_kotoba_line.setPreferredSize(nkls);
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        panel.add(new_kotoba_line, gbc);
        add_btn = new JButton("追加");
        add_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                add_exec();
            }
        });
        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        panel.add(add_btn, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(new JLabel("内容:"), gbc);
        new_naiyou_line = new JTextArea(5, 20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 5;
        gbc.gridheight = 2;
        panel.add(new JScrollPane(new_naiyou_line), gbc);
        gbc.gridx = 5;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        panel.add(new JLabel("漢字"), gbc);
        new_kanji_line = new JTextField(10);
        new_kanji_line.setPreferredSize(nkls);
        gbc.gridx = 5;
        gbc.gridy = 4;
        panel.add(new_kanji_line, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        panel.add(new JLabel("備考:"), gbc);
        new_bikou_line = new JTextArea(5, 20);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.gridheight = 2;
        panel.add(new JScrollPane(new_bikou_line), gbc);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        panel.add(new JLabel("音声ファイル"), gbc);
        new_mp3_line = new JTextField(20);
        new_mp3_line.setPreferredSize(nkls);
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 4;
        panel.add(new_mp3_line, gbc);
        attach_btn = new JButton("音声添付");
        attach_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                import_file();
            }
        });
        gbc.gridx = 5;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        panel.add(attach_btn, gbc);
        add(panel);
        setLocationRelativeTo(parentFrame);
        this.flag = flag;
    }
    void add_exec(){
        tuple t = new tuple();
        t.kotoba = new_kotoba_line.getText();
        t.imi = new_naiyou_line.getText();
        t.bikou = new_bikou_line.getText();
        t.kanji = new_kanji_line.getText();
        t.hinsi = comboBox.getSelectedIndex();
        byte[] mt = null;
        if (t.kanji.compareTo("") == 0){
            t.kanji = t.kotoba;
        }
        try{
            if (file != null) {
                FileInputStream ft = new FileInputStream(file);
                mt = new byte[(int) file.length()];
                ft.read(mt);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        dic = new dic_exec();
        boolean f = dic.add_kotoba(t, mt);
        if (!f){
            JOptionPane.showMessageDialog(null, "すでに存在する言葉です。", "お知らせ", JOptionPane.WARNING_MESSAGE);
        } else {
            flag[0] = true;
        }
    }
    void import_file(){
        JFileChooser fd = new JFileChooser();
        int t = fd.showOpenDialog(null);
        if (t == JFileChooser.APPROVE_OPTION) {
            file = fd.getSelectedFile();
            new_mp3_line.setText(file.getAbsolutePath());
        }
    }
}

class modify_ui extends JDialog {
    JTextField comboBox, new_kotoba_line, new_kanji_line, new_mp3_line;
    JButton add_btn, attach_btn;
    JTextArea new_naiyou_line, new_bikou_line;
    private dic_exec dic;
    File file = null;
    int hs;
    JCheckBox checkBox;
    boolean del_f = false;
    boolean[] flag;
    tuple b;
    public modify_ui(tuple t, boolean[] flag, tuple b, JFrame parentFrame) {
        super(parentFrame, "見出し語修正", true);
        setSize(500, 400);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        String hinsi;
        hs = t.hinsi;
        switch (t.hinsi){
            case 0:
                hinsi = "助詞";
                break;
            case 1:
                hinsi = "名詞";
                break;
            case 2:
                hinsi = "動詞";
                break;
            case 3:
                hinsi = "形容詞";
                break;
            case 4:
                hinsi = "副詞";
                break;
            default:
                hinsi = "その外";
        }
        comboBox = new JTextField(5);
        comboBox.setText(hinsi);
        comboBox.setEditable(false);
        Dimension nkls = comboBox.getPreferredSize();
        comboBox.setPreferredSize(nkls);
        nkls.height = 28;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        panel.add(comboBox, gbc);
        new_kotoba_line = new JTextField(15);
        new_kotoba_line.setText(t.kotoba);
        new_kotoba_line.setEditable(false);
        new_kotoba_line.setPreferredSize(nkls);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        panel.add(new_kotoba_line, gbc);
        add_btn = new JButton("修正");
        add_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modify_exec();
            }
        });
        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        panel.add(add_btn, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(new JLabel("内容:"), gbc);
        new_naiyou_line = new JTextArea(5, 20);
        new_naiyou_line.setText(t.imi);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 5;
        gbc.gridheight = 2;
        panel.add(new JScrollPane(new_naiyou_line), gbc);
        gbc.gridx = 5;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        panel.add(new JLabel("漢字"), gbc);
        new_kanji_line = new JTextField(10);
        new_kanji_line.setPreferredSize(nkls);
        new_kanji_line.setText(t.kanji);
        gbc.gridx = 5;
        gbc.gridy = 4;
        panel.add(new_kanji_line, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        panel.add(new JLabel("備考:"), gbc);
        new_bikou_line = new JTextArea(5, 20);
        new_bikou_line.setText(t.bikou);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.gridheight = 2;
        panel.add(new JScrollPane(new_bikou_line), gbc);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        panel.add(new JLabel("音声ファイル"), gbc);
        new_mp3_line = new JTextField(20);
        new_mp3_line.setPreferredSize(nkls);
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 4;
        panel.add(new_mp3_line, gbc);
        attach_btn = new JButton("音声添付");
        attach_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                import_file();
            }
        });
        gbc.gridx = 5;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        panel.add(attach_btn, gbc);
        checkBox = new JCheckBox("ファイル削除");
        checkBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    del_f = true;
                    new_mp3_line.setEnabled(false);
                    attach_btn.setEnabled(false);
                    new_mp3_line.setText("");
                    file = null;
                } else {
                    del_f = false;
                    new_mp3_line.setEnabled(true);
                    attach_btn.setEnabled(true);
                }
            }
        });
        dic = new dic_exec();
        if (dic.mp3_load(t.kotoba) == null){
            checkBox.setEnabled(false);
        }
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(checkBox, gbc);
        add(panel);
        setLocationRelativeTo(parentFrame);
        this.flag = flag;
        this.b = b;
    }
    void modify_exec(){
        tuple t = new tuple();
        t.kotoba = new_kotoba_line.getText();
        t.imi = new_naiyou_line.getText();
        t.bikou = new_bikou_line.getText();
        t.kanji = new_kanji_line.getText();
        t.hinsi = hs;
        byte[] mt = null;
        try{
            if (file != null) {
                FileInputStream ft = new FileInputStream(file);
                mt = new byte[(int) file.length()];
                ft.read(mt);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        dic = new dic_exec();
        dic.modify_kotoba(t, mt, del_f);
        flag[0] = true;
        b.kotoba = t.kotoba;
        b.imi = t.imi;
        b.bikou = t.bikou;
        b.kanji = t.kanji;
        b.hinsi = hs;
    }
    void import_file(){
        JFileChooser fd = new JFileChooser();
        int t = fd.showOpenDialog(null);
        if (t == JFileChooser.APPROVE_OPTION) {
            file = fd.getSelectedFile();
            new_mp3_line.setText(file.getAbsolutePath());
        }
    }
}
