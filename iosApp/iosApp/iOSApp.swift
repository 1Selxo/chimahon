import ChimahonShared
import SwiftUI
import UIKit

@main
struct ChimahonIOSApp: App {
    var body: some Scene {
        WindowGroup {
            ComposeView()
                .background(Color(uiColor: .systemBackground))
                .ignoresSafeArea(.keyboard)
        }
    }
}

private struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        let controller = MainViewControllerKt.MainViewController()
        controller.view.backgroundColor = .systemBackground
        return controller
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
    }
}
