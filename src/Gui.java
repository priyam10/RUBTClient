/*
 * @author Gurpreet Pannu, Michael Norris, Priyam Patel
 */


import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;



public class Gui extends JFrame implements ActionListener{
	private Timer timer;
	private RUBTClient client;
	JProgressBar progbar;
	JTable table;
	JPanel panel, info, container, settings, control;
	JLabel label;
	
	JLabel file_name, file_size, status;
	JLabel downloaded, uploaded, down_speed, up_speed, eta, ratio, peers_label;
	JLabel MAX_UPLOAD, MAX_DOWNLOAD, MAX_SLOTS;
	JTextField max_up_txt, max_dl_txt, max_slots_txt;
	JButton save, play_pause;
	
	public Gui(RUBTClient rubt){
		container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		setTitle("RUBTClient");
		panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
		//panel.setPreferredSize(new Dimension(300, 100));
		info = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
		settings = new JPanel(new GridLayout(3,2, 10, 0));
		info.setPreferredSize(new Dimension(280, 180));
		control = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
		//getContentPane().add(panel);
		//getContentPane().add(info);
		container.add(info);
		container.add(panel);
		container.add(settings);
		container.add(control);
		
		MAX_DOWNLOAD = new JLabel("Max Download Speed (KBps):");
		MAX_UPLOAD = new JLabel("Max Upload Speed (KBps):");
		MAX_SLOTS= new JLabel("Max Connections:");
		
		max_up_txt = new JTextField("", 20);
		max_dl_txt = new JTextField("", 20);
		max_slots_txt = new JTextField("", 20);
		
		settings.add(MAX_DOWNLOAD);
		settings.add(max_dl_txt);
		settings.add(MAX_UPLOAD);
		settings.add(max_up_txt);
		settings.add(MAX_SLOTS);
		settings.add(max_slots_txt);
		
		save = new JButton("Save Settings");
		play_pause = new JButton("Pause Torrent");
		
		control.add(save);
		control.add(play_pause);
		
		save.addActionListener(this);
		play_pause.addActionListener(this);
		
		getContentPane().add(container);
		this.client = rubt;
		status = new JLabel("<html><u>Status:</u> " + (rubt.amSeeding ? "Seeding" : "Downloading") + "</html>");
		file_name = new JLabel("<html><u>File:</u> " + rubt.torrentInfo.file_name + "</html>");
		file_size = new JLabel("<html><u>Size:</u> " + (rubt.torrentInfo.file_length / 1000000) + "MB</html>");
		
		downloaded = new JLabel("<html><u>Downloaded:</u> " + (rubt.bytesDownloaded / 1000000) + "MB</html>");
		uploaded = new JLabel("<html><u>Uploaded:</u> " + (double)Math.round(((double)client.bytesUploaded / 100000) * 100) / 100 + "KB</html>");
		ratio = new JLabel("<html><u>Ratio:</u> " + ((double)rubt.bytesUploaded / rubt.bytesDownloaded) + "</html>");
		peers_label = new JLabel("<html><u>Peers:</u> " + this.client.peers.size());
		info.add(status);
		info.add(file_name);
		info.add(file_size);
		info.add(downloaded);
		info.add(uploaded);
		info.add(ratio);
		info.add(peers_label);
		//down_speed = new JLabel("Download Speed: " + rubt.bytesUploaded);
		//Progress bar set up
		label = new JLabel("Progress: ");
		info.add(label);
		progbar = new JProgressBar();
		progbar.setPreferredSize( new Dimension( 300, 20 ) );
		progbar.setMaximum(100);
		progbar.setMinimum(0);
		progbar.setValue(this.client.getProgressBarPercent());
		progbar.setStringPainted(true);
		progbar.setBounds(20, 35, 260, 20);
		info.add(progbar);
		String[] columnNames = {"Peer IP", "Port", "Percentage Complete", "Downloaded From", "Uploaded To"};
		ArrayList<Peer> peers = client.peers;
		int peer_index = 0;
		Object[][] data = new Object[peers.size()][5];
		for(int row=0; row < peers.size(); row++){
			for(int col=0; col < 5; col++){
				switch(col){
					case 0:
						data[row][col] = peers.get(peer_index).peerIp;
						break;
					case 1:
						data[row][col] = new Integer(6881);
						break;
					case 2:
						data[row][col] = peers.get(peer_index).percentPeerHas + "%";
						break;
					case 3:
						data[row][col] = (peers.get(peer_index).fileBytesUploaded / 100000) + "KB";
						break;
					case 4:
						data[row][col] = (peers.get(peer_index).fileBytesDownloaded / 100000) + "KB";
						break;
				}
			}
			peer_index++;
		}
		table = new JTable(data, columnNames);
		//table.setEnabled(false);
		panel.add(new JScrollPane(this.table));
		//Make a timer for progress bar updates
		this.timer = new Timer(true);
		this.timer.scheduleAtFixedRate(new TimerTask(){
			// Update progress bar every 2 seconds
			@Override
			public void run() {
				progbar.setValue(client.getProgressBarPercent());
				if(status.getText().indexOf("Paused") == -1){
					status.setText("<html><u>Status:</u> " + (client.amSeeding ? "Seeding" : "Downloading") + "</html>");
				}
				downloaded.setText("<html><u>Downloaded:</u> " + (client.bytesDownloaded / 1000000) + "MB</html>");
				uploaded.setText("<html><u>Uploaded:</u> " + (double)Math.round(((double)client.bytesUploaded / 100000) * 100) / 100 + "KB</html>");
				ratio.setText("<html><u>Ratio:</u> " + (double)Math.round(((double)client.bytesUploaded / client.bytesDownloaded) * 100) / 100 + "</html>");
				peers_label.setText("<html><u>Peers:</u> " + client.peers.size() + "</html>");
			
				String[] columnNames = {"Peer IP", "Port", "Percentage Complete", "Downloaded From", "Uploaded To"};
				ArrayList<Peer> peers = client.peers;
				int peer_index = 0;
				Object[][] data = new Object[peers.size()][5];
				Object temp;
				for(int row=0; row < peers.size(); row++){
					for(int col=0; col < 5; col++){
						switch(col){
							case 0:
								temp = peers.get(peer_index).peerIp;
								table.setValueAt(temp, row, col);
								break;
							case 1:
								temp = new Integer(6881);
								table.setValueAt(temp, row, col);
								//data[row][col] = new Integer(6881);
								break;
							case 2:
								temp = peers.get(peer_index).percentPeerHas + "%";
								table.setValueAt(temp, row, col);
								//data[row][col] = peers.get(peer_index).percentPeerHas + "%";
								break;
							case 3:
								temp = peers.get(peer_index).fileBytesDownloaded / 100000 + "KB";
								table.setValueAt(temp, row, col);
								//data[row][col] = (peers.get(peer_index).fileBytesUploaded / 100000) + "KB";
								break;
							case 4:
								temp = peers.get(peer_index).fileBytesUploaded / 100000 + "KB";
								table.setValueAt(temp, row, col);
								//data[row][col] = (peers.get(peer_index).fileBytesDownloaded / 100000) + "KB";
								break;
						}
					}
					peer_index++;
				}
				table.repaint();
				//table = new JTable(data, columnNames);
			}
			
		}, 1, 2000);
		
		
		
	}
	
	public void actionPerformed(ActionEvent e){
		JButton src = (JButton)e.getSource();
		status.setText("");
		if(src == save){
			if(max_dl_txt.getText().length() != 0){
				if(Integer.parseInt(max_dl_txt.getText()) < 0){
					max_dl_txt.setText("");
					return;
				}
				client.DOWNLOAD_LIMIT = (Integer.parseInt(max_dl_txt.getText()) * 100000);
				max_dl_txt.setText("" + client.DOWNLOAD_LIMIT / 100000);
			}
			if(max_up_txt.getText().length() != 0){
				if(Integer.parseInt(max_up_txt.getText()) < 0){
					max_up_txt.setText("");
					return;
				}
				client.UPLOAD_LIMIT = (Integer.parseInt(max_up_txt.getText()) * 100000);
				max_up_txt.setText("" + client.UPLOAD_LIMIT / 100000);
			}
			if(max_slots_txt.getText().length() != 0){
				if(Integer.parseInt(max_slots_txt.getText()) < 0){
					max_slots_txt.setText("");
					return;
				}
				client.MAX_CONNECTIONS = (Integer.parseInt(max_slots_txt.getText()));
				max_slots_txt.setText("" + client.MAX_CONNECTIONS);
			}
		}
		if(src == play_pause){
			if(src.getText().equals("Pause Torrent")){
				//TODO: Client pause
				client.pause();
				status.setText("<html><u>Status:</u> Paused</html>");
				src.setText("Resume Torrent");
			}
			else if(src.getText().equals("Resume Torrent")){
				//TODO: undo client pause
				src.setText("Pause Torrent");
				status.setText("<html><u>Status:</u> " + (client.amSeeding ? "Seeding" : "Downloading") + "</html>");
			}
			
		}
	
	}


}
